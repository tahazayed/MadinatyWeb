package com.taha.alrehab.BusinessEntities;

import java.util.Date;

public class AlrehabNotification {
    private int _id;
    private String _title;
    private String _body;

    private Date _publishdate;

    private String _imageUrl;
    private String _imageThumbUrl;

    private int _type;

    public AlrehabNotification() {
    }

    public AlrehabNotification(int id,
                               String title,
                               Date publishdate,
                               String imageUrl,
                               String imageThumbUrl,
                               int type,
                               String body
    ) {
        this._id = id;
        this._title = title;
        this._publishdate = publishdate;
        this._imageUrl = imageUrl;
        this._imageThumbUrl = imageThumbUrl;
        this._type = type;
        this._body = body;
    }

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public String get_title() {
        return _title;
    }

    public void set_title(String _title) {
        this._title = _title;
    }


    public Date get_publishdate() {
        return _publishdate;
    }

    public void set_publishdate(Date _publishdate) {
        this._publishdate = _publishdate;
    }


    public String get_imageUrl() {
        return _imageUrl;
    }

    public void set_imageUrl(String _imageUrl) {
        this._imageUrl = _imageUrl;
    }

    public String get_imageThumbUrl() {
        return _imageThumbUrl;
    }

    public void set_imageThumbUrl(String _imageThumbUrl) {
        this._imageThumbUrl = _imageThumbUrl;
    }

    public int get_type() {
        return _type;
    }

    public void set_type(int _type) {
        this._type = _type;
    }

    public String get_body() {
        return _body;
    }

    public void set_body(String _body) {
        this._body = _body;
    }
}
