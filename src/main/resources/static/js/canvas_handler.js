// 캔버스가 추가될 컨테이너와, 생성된 캔버스를 관리하는 Map
const canvasContainer = document.getElementById('canvasContainer');
const canvasMap = new Map(); // 각 캔버스 ID를 키로 하고 fabric.Canvas 객체를 값으로 저장
let selectedCanvas = null; // 현재 선택된 캔버스
let maxCanvasIndex = 0; // 고유한 ID 생성을 위한 전역 카운터 / 가장 큰 canvas 인덱스를 저장할 변수

const csrfToken = document.querySelector('meta[name="_csrf"]').getAttribute('content');
const csrfHeader = document.querySelector('meta[name="_csrf_header"]').getAttribute('content');

// 페이지가 로드되면 canvasJson을 파싱하여 canvasData를 설정
document.addEventListener('DOMContentLoaded', function () {
  if (canvasDataString) {
    try {
      canvasData = JSON.parse(canvasDataString); // JSON 문자열을 JavaScript 객체로 변환
      if (canvasData && canvasData.canvases && canvasData.canvases.length > 0) {
        // canvasData가 존재하고 캔버스가 있을 경우 초기화
        initializeCanvases(canvasData);
      } else {
        // canvasData.canvases.length == 0 일떄
        initializeCanvases(canvasData);
      }
    } catch (error) {
      console.error('Error parsing canvas JSON:', error);
    }
  }
});

function initializeCanvases(canvasData) {
  if (canvasData && canvasData.canvases && canvasData.canvases.length > 0) {
    // canvasData가 있고, canvases 배열이 존재하는 경우 기존 캔버스 로드
    canvasData.canvases.forEach((canvasItem, index) => {
      const canvasId = canvasItem.id; // 각 캔버스의 고유 ID
      const canvasElement = document.createElement('canvas'); // 새로운 캔버스 HTML 요소 생성
      canvasElement.id = canvasId;
      canvasElement.width = 794; // 캔버스 너비 설정
      canvasElement.height = 1123; // 캔버스 높이 설정
      canvasElement.style.border = '1px solid #ccc'; // 캔버스 테두리 스타일 설정
      canvasContainer.appendChild(canvasElement); // 캔버스를 컨테이너에 추가

      // Fabric.js 캔버스 객체 생성
      const fabricCanvas = new fabric.Canvas(canvasId);

      // canvasItem.canvasData: JSON 데이터로 캔버스 객체 로드
      fabricCanvas.loadFromJSON(canvasItem.canvasData, function() {
        fabricCanvas.renderAll(); // 캔버스를 다시 렌더링하여 객체들을 화면에 반영
      });

      // canvasMap에 추가
      canvasMap.set(canvasId, fabricCanvas);

      // 캔버스를 클릭하면 선택 상태로 변경하는 이벤트 리스너 추가
      fabricCanvas.on('mouse:down', function () {
        setSelectedCanvas(fabricCanvas);
      });

      // canvasId에서 숫자를 추출하여 최대값을 갱신
      const match = canvasId.match(/paperCanvas-(\d+)/);
      if (match) {
        const indexNumber = parseInt(match[1], 10);
        if (indexNumber > maxCanvasIndex) {
          maxCanvasIndex = indexNumber;
        }
      }

      // 첫 번째 캔버스를 기본 선택
      if (!selectedCanvas) {
        setSelectedCanvas(fabricCanvas);
      }
      bindCanvasEvents(fabricCanvas);
    });
  } else {
    // canvasData가 없거나 canvases 배열이 비어 있는 경우
    console.log('No canvas data available or canvases array is empty.');

    // canvasContainer에 캔버스가 없다면 새로운 캔버스를 추가
    if (canvasContainer.children.length === 0) {
      // 새로운 캔버스 추가
      const canvasId = `paperCanvas-1`; // 고유 ID 설정
      const newCanvasElement = document.createElement('canvas'); // 새로운 캔버스 HTML 요소 생성
      newCanvasElement.id = canvasId;
      newCanvasElement.width = 794; // 캔버스 너비 설정
      newCanvasElement.height = 1123; // 캔버스 높이 설정
      newCanvasElement.style.border = '1px solid #ccc'; // 기본 테두리 스타일 적용
      canvasContainer.appendChild(newCanvasElement); // 캔버스를 컨테이너에 추가

      // Fabric.js 캔버스 객체 생성 및 관리 Map에 저장
      const fabricCanvas = new fabric.Canvas(canvasId);
      canvasMap.set(canvasId, fabricCanvas);

      // 캔버스를 클릭하면 선택 상태로 변경하는 이벤트 리스너 추가
      fabricCanvas.on('mouse:down', function () {
        setSelectedCanvas(fabricCanvas);
      });

      // 첫 번째 캔버스를 기본 선택
      if (!selectedCanvas) {
        setSelectedCanvas(fabricCanvas);
      }
      bindCanvasEvents(fabricCanvas);

      // maxCanvasIndex를 하나 증가시킴
      maxCanvasIndex++;
    }
  }
}

// 선택된 캔버스를 설정하는 함수
function setSelectedCanvas(fabricCanvas) {
  if (selectedCanvas) {
    selectedCanvas.lowerCanvasEl.style.border = '';
  }

  selectedCanvas = fabricCanvas;
  selectedCanvas.lowerCanvasEl.style.border = '2px solid blue';
}

// 캔버스 초기화 시 이벤트 바인딩 분리
function bindCanvasEvents(fabricCanvas) {
  let holdTimer = null;
  const holdDuration = 1000;

  // ✅ 캔버스 클릭 시 선택 처리
  fabricCanvas.on('mouse:down', function (e) {
    // 캔버스를 클릭했을 경우에도 setSelectedCanvas()가 호출되도록 처리
    setSelectedCanvas(fabricCanvas);

    const target = e.target;

    // 이미지 또는 텍스트박스가 아닐 경우 삭제 처리 안 함
    if (!target || (target.type !== 'image' && target.type !== 'textbox')) {
      return;
    }

    // 1초 동안 누르면 삭제 confirm
    holdTimer = setTimeout(() => {
      const confirmDelete = confirm('이 오브젝트를 삭제하시겠습니까?');
      if (confirmDelete) {
        fabricCanvas.remove(target);
        fabricCanvas.requestRenderAll();
      }
    }, holdDuration);
  });

  fabricCanvas.on('mouse:up', function () {
    if (holdTimer) {
      clearTimeout(holdTimer);
      holdTimer = null;
    }
  });

  fabricCanvas.on('mouse:out', function () {
    if (holdTimer) {
      clearTimeout(holdTimer);
      holdTimer = null;
    }
  });
}

// 새로운 Paper (캔버스) 추가
document.getElementById('addCanvasBtn').addEventListener('click', function () {
  maxCanvasIndex++; // 새로운 캔버스를 추가할 때마다 증가
  const canvasId = `paperCanvas-${maxCanvasIndex}`; // 항상 고유한 ID 보장
  const newCanvasElement = document.createElement('canvas'); // 새로운 캔버스 요소 생성
  newCanvasElement.id = canvasId;
  newCanvasElement.width = 794; // 너비 설정
  newCanvasElement.height = 1123; // 높이 설정
  newCanvasElement.style.border = '1px solid #ccc'; // 기본 테두리 스타일 적용
  canvasContainer.appendChild(newCanvasElement); // 캔버스를 컨테이너에 추가

  // Fabric.js 캔버스 객체 생성 및 관리 Map에 저장
  const fabricCanvas = new fabric.Canvas(canvasId);
  canvasMap.set(canvasId, fabricCanvas);

  // 캔버스를 클릭하면 선택 상태로 변경하는 이벤트 리스너 추가
  fabricCanvas.on('mouse:down', function () {
    setSelectedCanvas(fabricCanvas);
  });

  setSelectedCanvas(fabricCanvas); // 새로 추가된 캔버스를 자동으로 선택

  bindCanvasEvents(fabricCanvas);
});

// Paper 삭제 기능 (최소 1개 유지)
document.getElementById('deleteCanvasBtn').addEventListener('click', function () {
  if (canvasMap.size > 1 && selectedCanvas) { // 최소 하나는 유지해야 하므로 개수 확인
    const canvasId = selectedCanvas.lowerCanvasEl.id; // 선택된 캔버스의 ID 가져오기

    // `canvasContainer` 내부에서 해당 캔버스 요소 찾기
    const targetCanvas = canvasContainer.querySelector(`#${canvasId}`);

    if (targetCanvas) {
      selectedCanvas.dispose(); // Fabric.js 캔버스 객체 제거
      targetCanvas.remove(); // HTML DOM에서 제거
      canvasMap.delete(canvasId); // 관리 Map에서 제거
      selectedCanvas = null; // 선택된 캔버스를 초기화하여 자동 선택 방지
    }
  }
});

// 선택된 캔버스에 TextBox 추가
document.getElementById('addTextBoxBtn').addEventListener('click', function () {
  if (!selectedCanvas) return; // 선택된 캔버스가 없으면 실행하지 않음

  // 새 텍스트 박스 생성
  const text = new fabric.Textbox('텍스트 입력', {
    left: 150,
    top: 150,
    width: 200,
    fontSize: 20,
    borderColor: 'black',
    cornerColor: 'black',
    cornerSize: 10,
    transparentCorners: false, // 모서리를 불투명하게 설정하여 잘 보이도록 함
    splitByGrapheme: true // 줄바꿈
  });

  selectedCanvas.add(text); // 선택된 캔버스에 텍스트 박스를 추가
  selectedCanvas.setActiveObject(text); // 추가된 텍스트 박스를 선택 상태로 설정
  selectedCanvas.renderAll(); // 캔버스를 다시 렌더링하여 즉시 반영
});

// 키보드 이벤트 리스너 추가 (Delete 키로 선택된 객체 삭제)
document.addEventListener('keydown', function (event) {
  if (event.key === 'Delete') { // Delete 또는 Backspace 키 감지
    if (!selectedCanvas) return; // 선택된 캔버스가 없으면 실행하지 않음

    const activeObjects = selectedCanvas.getActiveObjects(); // 현재 선택된 객체들 가져오기

    if (activeObjects.length > 0) {
      activeObjects.forEach(obj => selectedCanvas.remove(obj)); // 모든 선택된 객체 삭제
      selectedCanvas.discardActiveObject(); // 선택 해제
      selectedCanvas.requestRenderAll(); // 캔버스를 다시 렌더링하여 즉시 반영
    }
  }
});

document.addEventListener("DOMContentLoaded", function () {
    const uploadImageBtn = document.getElementById("uploadImage");
    const uploadImageModal = document.getElementById("uploadImageModal");
    const closeModal = document.querySelector(".modal .closeUploadBtn");
    const saveImageBtn = document.getElementById("saveImageBtn");
    const imageInput = document.getElementById("imageInput");
    const imageContainer = document.getElementById("imageContainer");

    // ✅ UploadImage 버튼 클릭 시 모달 창 열기
    uploadImageBtn.addEventListener("click", function () {
        uploadImageModal.style.display = "block";
    });

    // ✅ 모달 닫기 버튼 클릭 시 모달 닫기
    closeModal.addEventListener("click", function () {
        uploadImageModal.style.display = "none";
    });

    // ✅ 이미지 업로드 기능
    saveImageBtn.addEventListener("click", function () {
        const file = imageInput.files[0];
        if (!file) {
            alert("이미지를 선택하세요.");
            return;
        }

        const formData = new FormData();
        formData.append("file", file);

        // S3 업로드 요청
        fetch("/api/s3/upload-image", {
            method: "POST",
            body: formData,
            headers: {
                [csrfHeader]: csrfToken
            }
        })
        .then(response => response.json())
        .then(data => {
            alert("이미지 업로드 성공!");
            uploadImageModal.style.display = "none";
            imageInput.value = ""; // 입력 필드 초기화
            loadImages(); // 업로드 후 이미지 목록 새로고침
        })
        .catch(error => {
            console.error("이미지 업로드 실패:", error);
            alert("이미지 업로드 실패");
        });
    });

    // ✅ S3에서 이미지 목록 가져오기
    function loadImages() {
        fetch("/api/s3/get-images", {
            method: "GET",
            headers: {
                "Content-Type": "application/json",
                [csrfHeader]: csrfToken
            }
        })
        .then(response => response.json())
        .then(images => {
            imageContainer.replaceChildren(); // 기존 이미지 제거 (더 안전한 방식)

            images.forEach(imageUrl => {
                // 이미지 컨테이너 생성 (삭제 버튼과 함께)
                const imageWrapper = document.createElement("div");
                imageWrapper.classList.add("image-wrapper");

                // 이미지 요소 생성
                const imgElement = document.createElement("img");
                imgElement.src = imageUrl;
                imgElement.classList.add("image-thumbnail");
                imgElement.crossOrigin = "anonymous"; // ✅ 이미지 엘리먼트에도 명시적으로 지정

                // 이미지 클릭 시 Canvas에 추가
                imgElement.addEventListener("click", () => {
                    if (!selectedCanvas) return;
                    fabric.Image.fromURL(
                        imageUrl,
                        function (img) {
                            img.set({
                                left: 100,
                                top: 100,
                                selectable: true
                            });
                            // ✅ 이벤트 등록 예시
                            img.on('mousedown', () => {
                            console.log('이미지 클릭됨');
                            });
                            selectedCanvas.add(img);
                            selectedCanvas.renderAll();
                        },
                        { crossOrigin: "anonymous" } // ✅ 더 확실한 방식
                    );
                });

                // ❌ 삭제 버튼 생성
                const deleteButton = document.createElement("button");
                deleteButton.innerHTML = "X";
                deleteButton.classList.add("delete-btn");
                deleteButton.addEventListener("click", () => deleteImage(imageUrl, imageWrapper));

                // 이미지 컨테이너에 추가
                imageWrapper.appendChild(imgElement);
                imageWrapper.appendChild(deleteButton);
                imageContainer.appendChild(imageWrapper);
            });
        })
        .catch(error => console.error("이미지 목록 불러오기 실패:", error));
    }
    // ✅ 이미지 삭제 함수
    function deleteImage(imageUrl, imageElement) {
        const confirmDelete = confirm("정말 이 이미지를 삭제하시겠습니까? 🗑️");
        if (!confirmDelete) return;

        const url = new URL(imageUrl);
        const fileName = url.pathname.split('/').pop(); // ✅ 안전한 방식

        fetch(`/api/s3/delete-image/${fileName}`, {
            method: "DELETE",
            headers: {
                "Content-Type": "application/json",
                [csrfHeader]: csrfToken
            }
        })
            .then(response => {
                if (response.ok) {
                    imageElement.remove(); // 화면에서 이미지 제거
                    alert("이미지가 삭제되었습니다.");
                } else {
                    alert("이미지 삭제 실패");
                }
            })
            .catch(error => console.error("이미지 삭제 오류:", error));
    }
    // 페이지 로드 시 이미지 불러오기
    loadImages();
});

// 🔹 캔버스들을 이미지로 저장하는 기능 (한 번에 여러 개 저장)
function captureCanvasAsImageList(bundleId, canvasIdList) {
  const imageDataArray = [];

  canvasIdList.forEach(canvasId => {
    const canvas = document.getElementById(canvasId);
    if (!canvas) return;

    const dataURL = canvas.toDataURL('image/png');
    imageDataArray.push({ canvasId: canvasId, imageData: dataURL });
  });

  fetch(`/canvas/save-thumbnails/${bundleId}`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
      [csrfHeader]: csrfToken  // CSRF 토큰 추가
    },
    body: JSON.stringify(imageDataArray) // images 키 없이 바로 리스트
  })
  .then(response => {
    if (response.ok) {
      console.log('All thumbnails saved successfully');
    } else {
      throw new Error('Failed to save thumbnails');
    }
  })
  .catch(error => console.error('Error saving thumbnails:', error));
}


function saveAllCanvas(bundleId) {
  if (canvasMap.size === 0) return; // 캔버스가 없으면 실행하지 않음

  const canvasArray = [];

  // 🔽 canvasId에서 숫자 기준으로 정렬
  const sortedEntries = Array.from(canvasMap.entries()).sort(([idA], [idB]) => {
    const numA = parseInt(idA.split('-')[1], 10);
    const numB = parseInt(idB.split('-')[1], 10);
    return numA - numB;
  });

  const canvasIdList = [];

  sortedEntries.forEach(([canvasId, fabricCanvas]) => {
    const canvasJson = fabricCanvas.toJSON();
    canvasArray.push({ id: canvasId, canvasData: canvasJson });
    canvasIdList.push(canvasId); // ID 목록 수집
  });

  // 🔹 썸네일 저장: canvasId 목록만 전달하여 한 번에 처리
  captureCanvasAsImageList(bundleId, canvasIdList);

  fetch('/canvas/save', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
      [csrfHeader]: csrfToken  // CSRF 토큰 추가
    },
    body: JSON.stringify({
      bundleId: bundleId,
      canvases: canvasArray
    })
  })
  .then(response => {
    if (response.ok) {
      window.location.href = '/bundle/detail/' + bundleId;
    } else {
      throw new Error('Failed to save canvases');
    }
  })
  .catch(error => console.error('Error saving canvases:', error));
}


//pdf출력 event
/*------------------------------------------------------------------------------------------------------------------------*/
// PDF로 저장 기능
document.getElementById('printCanvasBtn').addEventListener('click', async function () {
  const pdf = new jspdf.jsPDF('p', 'pt', 'a4');
  const canvasEntries = Array.from(canvasMap.entries());

  for (let i = 0; i < canvasEntries.length; i++) {
    const [canvasId, fabricCanvas] = canvasEntries[i];

    // 캔버스에서 이미지 데이터 생성 (PNG 형식)
    const dataUrl = fabricCanvas.toDataURL({
      format: 'png',
      multiplier: 2 // 고해상도 출력
    });

    // 이미지가 로드될 때까지 대기
    const image = new Image();
    image.src = dataUrl;

    await new Promise((resolve) => {
      image.onload = () => {
        const pageWidth = 595.28;  // A4 너비 (pt)
        const pageHeight = 841.89; // A4 높이 (pt)

        const imgWidth = pageWidth;
        const imgHeight = (image.height * pageWidth) / image.width;

        pdf.addImage(image, 'PNG', 0, 0, imgWidth, imgHeight);

        // 다음 페이지 추가 (마지막 페이지 제외)
        if (i < canvasEntries.length - 1) {
          pdf.addPage();
        }

        resolve();
      };
    });
  }

  pdf.save('canvas-bundle.pdf');
});
