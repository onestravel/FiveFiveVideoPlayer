package cn.onestravel.fivefiveplayer

import android.net.Uri


/**
 * @author onestravel
 * @createTime 2020-03-20
 * @description TODO
 */
class MediaDataSource {
    var header: Map<String, String>? = null
    var title: String = ""
    var uri: Uri? = null
    var isLooping: Boolean = false

    constructor() {}
    constructor(uri: Uri?) {
        this.uri = uri
    }

    constructor(title: String, uri: Uri?) {
        this.title = title
        this.uri = uri
    }

    constructor(title: String, uri: Uri?, header: Map<String, String>?) {
        this.title = title
        this.uri = uri
        this.header = header
    }

    constructor(title: String, uri: Uri?, loop: Boolean) {
        this.title = title
        this.uri = uri
        this.isLooping = loop
    }

    constructor(title: String, uri: Uri?, header: Map<String, String>?, loop: Boolean) {
        this.title = title
        this.uri = uri
        this.header = header
        this.isLooping = loop
    }

}