/**
 * Copyright (c) 2015-2021, Michael Yang 杨福海 (fuhai999@gmail.com).
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.jboot.web.directive.base;

import com.jfinal.plugin.activerecord.Page;
import com.jfinal.template.Env;
import com.jfinal.template.io.Writer;
import com.jfinal.template.stat.Scope;
import io.jboot.utils.StrUtil;

import java.util.ArrayList;
import java.util.List;


public abstract class PaginateDirectiveBase extends JbootDirectiveBase {

    private static final String PREVIOUS_CLASS_KEY = "previousClass";
    private static final String NEXT_CLASS_KEY = "nextClass";
    private static final String ACTIVE_CLASS_KEY = "activeClass";
    private static final String DISABLED_CLASS_KEY = "disabledClass";
    private static final String ONLY_SHOW_PREVIOUS_AND_NEXT_KEY = "onlyShowPreviousAndNext";

    private static final String PREVIOUS_TEXT_KEY = "previousText";
    private static final String NEXT_TEXT_KEY = "nextText";
    private static final String PAGE_ITEMS_NAME_KEY = "pageItemsName";
    private static final String PAGE_DATA_KEY = "pageData";
    private static final String SIBLINGS_ITEM_COUNT_KEY = "siblingsItemCount";
    private static final String START_ITEM_COUNT_KEY = "startItemCount";
    private static final String END_ITEM_COUNT_KEY = "endItemCount";


    private static final String DEFAULT_PREVIOUS_CLASS = "previous";
    private static final String DEFAULT_NEXT_CLASS = "next";
    private static final String DEFAULT_ACTIVE_CLASS = "active";
    private static final String DEFAULT_DISABLED_CLASS = "disabled";

    private static final String DEFAULT_PREVIOUS_TEXT = "上一页";
    private static final String DEFAULT_NEXT_TEXT = "下一页";
    private static final String DEFAULT_PAGE_ITEMS_NAME = "pages";
    private static final String DEFAULT_PAGE_DATA_KEY = "pageData";

    private static final String JAVASCRIPT_TEXT = "javascript:;";
    private static final String ELLIPSIS_TEXT = "...";

    private static final int SIBLINGS_ITEM_COUNT = 2;
    private static final int START_ITEM_COUNT = 1;
    private static final int END_ITEM_COUNT = 1;


    @Override
    public void onRender(Env env, Scope scope, Writer writer) {

        String previousClass = getPara(PREVIOUS_CLASS_KEY, scope, DEFAULT_PREVIOUS_CLASS);
        String nextClass = getPara(NEXT_CLASS_KEY, scope, DEFAULT_NEXT_CLASS);
        String activeClass = getPara(ACTIVE_CLASS_KEY, scope, DEFAULT_ACTIVE_CLASS);
        String disabledClass = getPara(DISABLED_CLASS_KEY, scope, DEFAULT_DISABLED_CLASS);
        boolean onlyShowPreviousAndNext = getParaToBool(ONLY_SHOW_PREVIOUS_AND_NEXT_KEY, scope, false);

        String previousText = getPara(PREVIOUS_TEXT_KEY, scope, DEFAULT_PREVIOUS_TEXT);
        String nextText = getPara(NEXT_TEXT_KEY, scope, DEFAULT_NEXT_TEXT);
        String pageItemsName = getPara(PAGE_ITEMS_NAME_KEY, scope, DEFAULT_PAGE_ITEMS_NAME);

        String pageDataKey = getPara(PAGE_DATA_KEY, scope, DEFAULT_PAGE_DATA_KEY);

        int siblingsItemCount = getParaToInt(SIBLINGS_ITEM_COUNT_KEY, scope, SIBLINGS_ITEM_COUNT);
        if (siblingsItemCount < 1) {
            siblingsItemCount = SIBLINGS_ITEM_COUNT;
        }


        int startItemCount = getParaToInt(START_ITEM_COUNT_KEY, scope, START_ITEM_COUNT);
        if (startItemCount < 1) {
            startItemCount = START_ITEM_COUNT;
        }


        int endItemCount = getParaToInt(END_ITEM_COUNT_KEY, scope, END_ITEM_COUNT);
        if (endItemCount < 1) {
            endItemCount = END_ITEM_COUNT;
        }


        Page<?> page = getPage(env, scope, writer);

        int currentPageNumber = page == null ? 1 : page.getPageNumber();
        int totalPage = page == null ? 0 : page.getTotalPage();

        if (totalPage == 0) {
            return;
        }

        if (currentPageNumber > totalPage) {
            currentPageNumber = totalPage;
        }

        int startPage = currentPageNumber - siblingsItemCount;
        if (startPage < 1) {
            startPage = 1;
        }

        int endPage = currentPageNumber + siblingsItemCount;
        if (endPage > totalPage) {
            endPage = totalPage;
        }

        List<PaginateItem> pages = new ArrayList<PaginateItem>();
        if (currentPageNumber == 1) {
            pages.add(new PaginateDirectiveBase.PaginateItem(previousClass + StrUtil.SPACE + disabledClass, JAVASCRIPT_TEXT, previousText));
        } else {
            pages.add(new PaginateDirectiveBase.PaginateItem(previousClass, getUrl(currentPageNumber - 1, env, scope, writer), previousText));
        }

        if (!onlyShowPreviousAndNext) {

            //开始页码
            for (int i = 1; i <= startItemCount; i++) {
                if (i < currentPageNumber - siblingsItemCount) {
                    pages.add(new PaginateDirectiveBase.PaginateItem(StrUtil.EMPTY, getUrl(i, env, scope, writer), i));
                }
            }

            //省略号
            if (currentPageNumber > startItemCount + siblingsItemCount + 1) {
                pages.add(new PaginateDirectiveBase.PaginateItem(disabledClass, JAVASCRIPT_TEXT, ELLIPSIS_TEXT));
            }


            //中间页码
            for (int i = startPage; i <= endPage; i++) {
                if (currentPageNumber == i) {
                    pages.add(new PaginateDirectiveBase.PaginateItem(activeClass, JAVASCRIPT_TEXT, i));
                } else {
                    pages.add(new PaginateDirectiveBase.PaginateItem(StrUtil.EMPTY, getUrl(i, env, scope, writer), i));
                }
            }


            //省略号
            if (currentPageNumber < totalPage - siblingsItemCount - endItemCount) {
                pages.add(new PaginateDirectiveBase.PaginateItem(disabledClass, JAVASCRIPT_TEXT, ELLIPSIS_TEXT));
            }

            //后边页码
            for (int i = (endItemCount - 1); i >= 0; i--) {
                if (i < totalPage - (currentPageNumber + siblingsItemCount)) {
                    pages.add(new PaginateDirectiveBase.PaginateItem(StrUtil.EMPTY, getUrl(totalPage - i, env, scope, writer), totalPage - i));
                }

            }
        }


        if (currentPageNumber == totalPage) {
            pages.add(new PaginateDirectiveBase.PaginateItem(nextClass + StrUtil.SPACE + disabledClass, JAVASCRIPT_TEXT, nextText));
        } else {
            pages.add(new PaginateDirectiveBase.PaginateItem(nextClass, getUrl(currentPageNumber + 1, env, scope, writer), nextText));
        }

        scope.setLocal(pageItemsName, pages);
        scope.setLocal(pageDataKey, page);

        renderBody(env, scope, writer);
    }


    protected abstract String getUrl(int pageNumber, Env env, Scope scope, Writer writer);

    protected abstract Page<?> getPage(Env env, Scope scope, Writer writer);


    @Override
    public boolean hasEnd() {
        return true;
    }


    public static class PaginateItem {
        private String style;
        private String url;
        private String text;

        public PaginateItem(String style, String url, Object text) {
            this.style = style;
            this.url = url;
            this.text = String.valueOf(text);
        }

        public String getStyle() {
            return style;
        }

        public void setStyle(String style) {
            this.style = style;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }
    }
}
