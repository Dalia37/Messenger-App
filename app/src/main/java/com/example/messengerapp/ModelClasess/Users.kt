package com.example.messengerapp.ModelClasess

class Users {

    //uid must me as database firebase
    private var uid :String ?= null
    private var username  :String ?=null
    private var profile   :String ?=null
    private var cover  :String ?=null
    private var search  :String ?=null
    private var status  :String ?=null
    private var facebook  :String ?=null
    private var instagram  :String ?=null
    private var website  :String ?=null

    constructor()


    constructor(
        uid: String?,
        username: String?,
        profile: String?,
        cover: String?,
        search: String?,
        status: String?,
        facebook: String?,
        instagram: String?,
        website: String?
    ) {
        this.uid = uid
        this.username = username
        this.profile = profile
        this.cover = cover
        this.search = search
        this.status = status
        this.facebook = facebook
        this.instagram = instagram
        this.website = website
    }

    fun getUID():String?{
        return uid
    }
    fun setUID(uid :String){
        this.uid = uid
    }

    fun getUserName():String?{
        return username
    }
    fun setUserName(username :String){
        this.username = username
    }

    fun getCover():String?{
        return cover
    }
    fun setCover(civer :String){
        this.cover = cover
    }

    fun getProfile():String?{
        return profile
    }
    fun setProfile(profile :String){
        this.profile = profile
    }


    fun getSearch():String?{
        return search
    }
    fun setSearch(search :String){
        this.search = search
    }

    fun getStatus():String?{
        return status
    }
    fun setStatus(status :String){
        this.status = status
    }

    fun getFacebook():String?{
        return facebook
    }
    fun setFacebook(profile :String){
        this.facebook = facebook
    }

    fun getInstagram():String?{
        return instagram
    }
    fun setInstagram(instagram :String){
        this.instagram = instagram
    }

    fun getWebsite():String?{
        return website
    }
    fun setWebsite(website :String){
        this.website = website
    }



}