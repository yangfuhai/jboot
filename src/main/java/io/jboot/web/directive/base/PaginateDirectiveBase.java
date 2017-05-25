/**
 * Copyright (c) 2015-2017, Michael Yang 杨福海 (fuhai999@gmail.com).
 * <p>
 * Licensed under the GNU Lesser General Public License (LGPL) ,Version 3.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.gnu.org/licenses/lgpl-3.0.txt
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
import com.jfinal.template.stat.Scope;
import io.jboot.web.RequestManager;

import java.io.Writer;
import java.util.ArrayList;
import java.util.List;


public abstract class PaginateDirectiveBase extends JbootDirectiveBase {


    private String previousClass = "previous";
    private String nextClass = "next";
    private String activeClass = "active";
    private String disabledClass = "disabled";
    private String anchor = "anchor";
    private boolean onlyShowPreviousAndNext = false;

    @Override
    public void exec(Env env, Scope scope, Writer writer) {

        previousClass = getParam("previousClass", "previous", scope);
        nextClass = getParam("nextClass", "next", scope);
        activeClass = getParam("activeClass", "active", scope);
        disabledClass = getParam("disabledClass", "disabled", scope);
        anchor = getParam("anchor", null, scope);
        onlyShowPreviousAndNext = getParam("onlyShowPreviousAndNext", false, scope);

        String previousText = getParam("previousText", "上一页", scope);
        String nextText = getParam("nextText", "下一页", scope);

        Page<?> page = RequestManager.me().getRequestAttr("pageData");

        int currentPage = page.getPageNumber();
        int totalPage = page.getTotalPage();

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
            pages.add(new PaginateDirectiveBase.PaginateItem(previousClass, getUrl(currentPage - 1), previousText));
        }

        if (currentPage > 8 && !onlyShowPreviousAndNext) {
            pages.add(new PaginateDirectiveBase.PaginateItem("", getUrl(1), "1"));
            pages.add(new PaginateDirectiveBase.PaginateItem("", getUrl(2), "2"));
            pages.add(new PaginateDirectiveBase.PaginateItem(disabledClass, "javascript:;", "..."));
        }

        if (!onlyShowPreviousAndNext) {
            for (int i = startPage; i <= endPage; i++) {
                if (currentPage == i) {
                    pages.add(new PaginateDirectiveBase.PaginateItem(activeClass, "javascript:;", i));
                } else {
                    pages.add(new PaginateDirectiveBase.PaginateItem("", getUrl(i), i));
                }
            }
        }

        if ((totalPage - currentPage) >= 8 && !onlyShowPreviousAndNext) {
            pages.add(new PaginateDirectiveBase.PaginateItem(disabledClass, "javascript:;", "..."));
            pages.add(new PaginateDirectiveBase.PaginateItem("", getUrl(totalPage - 1), totalPage - 1));
            pages.add(new PaginateDirectiveBase.PaginateItem("", getUrl(totalPage), totalPage));
        }

        if (currentPage == totalPage) {
            pages.add(new PaginateDirectiveBase.PaginateItem(nextClass + " " + disabledClass, "javascript:;", nextText));
        } else {
            pages.add(new PaginateDirectiveBase.PaginateItem(nextClass, getUrl(currentPage + 1), nextText));
        }

        scope.setLocal("pages", pages);
        stat.exec(env, scope, writer);
    }


    public String getPrevious() {
        return previousClass;
    }

    public String getNext() {
        return nextClass;
    }

    public String getDisabled() {
        return disabledClass;
    }

    public String getAnchor() {
        return anchor;
    }

    protected abstract String getUrl(int pageNumber);


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

    @Override
    public boolean hasEnd() {
        return true;
    }
}
