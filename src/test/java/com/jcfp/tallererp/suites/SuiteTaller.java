package com.jcfp.tallererp.suites;

import com.jcfp.tallererp.controller.OrdenTrabajoControllerTest;
import com.jcfp.tallererp.security.AuthControllerTest;
import com.jcfp.tallererp.service.OrdenTrabajoServiceImplTest;
import org.junit.platform.suite.api.Suite;
import org.junit.platform.suite.api.SelectClasses;

@Suite
@SelectClasses({
        OrdenTrabajoServiceImplTest.class,
        OrdenTrabajoControllerTest.class,
        AuthControllerTest.class
})public class SuiteTaller {
}
