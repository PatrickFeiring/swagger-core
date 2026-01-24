package io.swagger.v3.core.util;

import io.swagger.v3.oas.models.media.Schema;
import org.approvaltests.Approvals;
import org.approvaltests.core.Options;

public class ApprovalUtils {

    public static void verifyInline(Schema model, String expected) {
//        Approvals.verify(toJson31(model), new Options().inline(expected));
    }
}
