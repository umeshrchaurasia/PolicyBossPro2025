package com.policyboss.policybosspro.core.oldWayApi;

import com.policyboss.policybosspro.core.response.APIResponse;

/**
 * Created by Rajeev Ranjan on 22/01/2018.
 */

public interface IResponseSubcriber {

    void OnSuccess(APIResponse response, String message);

    void OnFailure(Throwable t);

}