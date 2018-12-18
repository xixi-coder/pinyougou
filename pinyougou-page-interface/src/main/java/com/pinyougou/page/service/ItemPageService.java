package com.pinyougou.page.service;

import java.io.IOException;

public interface ItemPageService {

    public boolean genItemHtml(Long goodsId) throws IOException;
    public boolean deleteItemHtml(Long[] goodsId) throws IOException;
}
