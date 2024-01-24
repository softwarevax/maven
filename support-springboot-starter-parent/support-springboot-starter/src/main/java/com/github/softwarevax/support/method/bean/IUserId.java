package com.github.softwarevax.support.method.bean;

import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;

public interface IUserId {
    Serializable getUserId(HttpServletRequest request);
}
