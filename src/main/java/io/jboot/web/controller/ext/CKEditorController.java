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
package io.jboot.web.controller.ext;

import com.jfinal.upload.UploadFile;
import io.jboot.web.controller.JbootController;

/**
 * 专为 CKEditor 编辑器写的controller
 */
public abstract class CKEditorController extends JbootController {


    /**
     * 进行认证
     *
     * @return 认证成功返回true，认证失败返回false，返回false将不进行上传
     */
    public abstract boolean doAuthentication();

    /**
     * 进行开始上传成功的操作，
     *
     * @param uploadFile
     * @return 最终网站能访问的url地址（路径）
     */
    public abstract String doUpload(UploadFile uploadFile);


    /**
     * 访问 index 或 upload 都能上传
     */
    public void index() {
        upload();
    }


    /**
     * 开始上传
     */
    public void upload() {
        if (!isMultipartRequest()) {
            renderError(404);
            return;
        }


        if (!doAuthentication()) {
            renderError(404);
            return;
        }

        Integer CKEditorFuncNum = getParaToInt("CKEditorFuncNum");
        if (CKEditorFuncNum == null) {
            renderError(404);
            return;
        }

        UploadFile uploadFile = getFile();
        if (uploadFile == null) {
            renderText("请提交上传的文件。");
            return;
        }

        String path = doUpload(uploadFile);


        /**
         * <script type="text/javascript">
         window.parent.CKEDITOR.tools.callFunction("0", "", "");
         </script>
         */
        int funcNum = getParaToInt("CKEditorFuncNum");
        StringBuilder textBuilder = new StringBuilder("<script type=\"text/javascript\">");
        textBuilder.append("window.parent.CKEDITOR.tools.callFunction(\"" + funcNum + "\", \"" + path + "\", \"\");");
        textBuilder.append("</script>");
        renderHtml(textBuilder.toString());
    }


}
