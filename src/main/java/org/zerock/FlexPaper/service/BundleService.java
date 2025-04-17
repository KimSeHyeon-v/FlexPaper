package org.zerock.FlexPaper.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.zerock.FlexPaper.domain.Bundle;
import org.zerock.FlexPaper.domain.FPUser;
import org.zerock.FlexPaper.dto.BundleDTO;

public interface BundleService {

    // Bundle 생성 로직
    public void createBundle(BundleDTO bundleDTO, FPUser user);
    public BundleDTO getBundleWithCanvas(Long bno);
    public void modifyBundle(BundleDTO bundleDTO);
    public void removeBundle(Long bno);
    public Page<Bundle> searchUserBundles(String username, String keyword, Pageable pageable);
}
