package com.suapp.dcdownloader.retrofit.network;

import java.util.Map;

/**
 * @author wangwei on 2017/6/3.
 *         wangwei@jiandaola.com
 */
public interface PublicParamsGenerator<T, W> {

    Map<T, W> generate();
}
