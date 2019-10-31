/**
 * Copyright (c) 2015-2019, Michael Yang 杨福海 (fuhai999@gmail.com).
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

import java.util.ArrayList;
import java.util.List;


public abstract class PaginateDirectiveBase extends JbootDirectiveBase {


    @Override
    public void onRender(Env env, Scope scope, Writer writer) {

        String previousClass = getPara("previousClass", scope, "previous");
        String nextClass = getPara("nextClass", scope, "next");
        String activeClass = getPara("activeClass", scope, "active");
        String disabledClass = getPara("disabledClass", scope, "disabled");
        boolean onlyShowPreviousAndNext = getPara("onlyShowPreviousAndNext", scope, false);

        String previousText = getPara("previousText", scope, "上一页");
        String nextText = getPara("nextText", scope, "下一页");
        String pageItemsName = getPara("pageItemsName", scope, "page");


        Page<?> page = getPage(env, scope, writer);

        int currentPage = page == null ? 1 : page.getPageNumber();
        int totalPage = page == null ? 1 : page.getTotalPage();

        if ((totalPage <= 0) || (currentPage > totalPage)) {
            return;
        }

        int startPage = currentPage - 4;
        if (startPage < 1) {
            startPage = 1;
        }
        int endPage = currentPage + 4;
        if (endPage > totalPage) {
            endPage = totalPage;
        }

        if (currentPage <= 8) {
            startPage = 1;
        }

        if ((totalPage - currentPage) < 8) {
            endPage = totalPage;
        }

        List<PaginateItem> pages = new ArrayList<PaginateItem>();
        if (currentPage == 1) {
            pages.add(new PaginateDirectiveBase.PaginateItem(previousClass + " " + disabledClass, "javascript:;", previousText));
        } else {
            pages.add(new PaginateDirectiveBase.PaginateItem(previousClass, getUrl(currentPage - 1, env, scope, writer), previousText));
        }

        if (currentPage > 8 && !onlyShowPreviousAndNext) {
            pages.add(new PaginateDirectiveBase.PaginateItem("", getUrl(1, env, scope, writer), "1"));
            pages.add(new PaginateDirectiveBase.PaginateItem("", getUrl(2, env, scope, writer), "2"));
            pages.add(new PaginateDirectiveBase.PaginateItem(disabledClass, "javascript:;", "..."));
        }

        if (!onlyShowPreviousAndNext) {
            for (int i = startPage; i <= endPage; i++) {
                if (currentPage == i) {
                    pages.add(new PaginateDirectiveBase.PaginateItem(activeClass, "javascript:;", i));
                } else {
                    pages.add(new PaginateDirectiveBase.PaginateItem("", getUrl(i, env, scope, writer), i));
                }
            }
        }

        if ((totalPage - currentPage) >= 8 && !onlyShowPreviousAndNext) {
            pages.add(new PaginateDirectiveBase.PaginateItem(disabledClass, "javascript:;", "..."));
            pages.add(new PaginateDirectiveBase.PaginateItem("", getUrl(totalPage - 1, env, scope, writer), totalPage - 1));
            pages.add(new PaginateDirectiveBase.PaginateItem("", getUrl(totalPage, env, scope, writer), totalPage));
        }

        if (currentPage == totalPage) {
            pages.add(new PaginateDirectiveBase.PaginateItem(nextClass + " " + disabledClass, "javascript:;", nextText));
        } else {
            pages.add(new PaginateDirectiveBase.PaginateItem(nextClass, getUrl(currentPage + 1, env, scope, writer), nextText));
        }

        scope.setLocal(pageItemsName, pages);
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

        public PaginateItem(String style, String url, String text) {
            this.style = style;
            this.url = url;
            this.text = text;
        }

        public PaginateItem(String style, String url, int text) {
            this.style = style;
            this.url = url;
            this.text = text + "";
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
