package com.guc.fileuploadtest.utils.fileuploaduitl;

import java.util.List;

/**
 */

public interface UploadFileCallback {

    void onSuccess(Object tag, List<BeanUploadFile> uploadFile);

    void onFailure(Object tag);


}
