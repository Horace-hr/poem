//配置参数
var NP_config = {
    loadingImg : 'img/NP_loading.gif' ,
    showClass : 'page-show' , //单页显示的class
    baseClass : 'page' , //单页基础class
    menuId : 'menu' ,  //菜单ID
    menuItemClass : 'item' , //菜单子级的class
    menuShowClass : 'cur' ,  //显示菜单的class
    host : getHost() , //网站根路径
    service : getHost() , //api接口的请求路径
    start : 'N.' , //开始标签
    end : '.L' ,   //结束标签
    loginPageId : 'page_login' , //登陆页面ID
    pageRoute : '/page' ,
    defaultPage:'index',
    defaultParams:''
} ;

var NP_DATA = {
    startParams : null ,
    historyAction : null ,
    pageParams : {} ,
    cookieUid : null
} ;

/**********************************TOOL 工具类方法**********************************************/

/*********获取当前URL中的参数 返回JSON对象 **********/
function getHost() {
    var h = location.href ;
    var p = location.pathname ;
    return h.substr(0, h.indexOf(p));
}
function t_getUrlParamsJson(){
    var paramsJson= {
        page : NP_config.defaultPage ,
        params : NP_config.defaultParams
    } ;
    var href = location.href ;
    var queryStr = location.search ;
    NP_DATA['historyAction'] = href.replace(queryStr , '');
    if(!queryStr) return paramsJson ;
    var arr = location.search.substring(1).split("&") ;
    for( var i = 0 ; i< arr.length ; i++){
        var pairs = arr[i] ;
        var pos = pairs.indexOf('=');
        if( pos == -1 )   continue;
        paramsJson[ pairs.substring(0,pos) ] = decodeURI( pairs.substring(pos+1) );
    }
    return paramsJson ;
}

function generateUUID() {
    var d = new Date().getTime();
    return 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function(c) {
        var r = (d + Math.random()*16)%16 | 0;
        d = Math.floor(d/16);
        return (c=='x' ? r : (r&0x3|0x8)).toString(16);
    });
}

function setCookie(name,value,days) {
    if (days) {
        var date = new Date();
        date.setTime(date.getTime()+(days*24*60*60*1000));
        var expires = "; expires="+date.toGMTString();
    }
    else expires = "";
    document.cookie = name+"="+value+expires+"; path=/";
}

function getCookie(name) {
    var nameEQ = name + "=";
    var ca = document.cookie.split(';');
    for(var i=0;i < ca.length;i++) {
        var c = ca[i];
        while (c.charAt(0)==' ') c = c.substring(1,c.length);
        if (c.indexOf(nameEQ) == 0) return c.substring(nameEQ.length,c.length);
    }
    return null ;
}

//功能组件
var NP_component = {
    loading : function (isLoading) {
        var loading = $('#NP_loading');
        if(loading.length == 0 ){
            loading = $('<div id="NP_loading" style="position: fixed;top: 0;left: 0;right: 0;bottom: 0;z-index: 20170107;text-align: center;"><img style="height: 26px;width: 26px;position: absolute;top: 50%;margin-top: -13px;left: 50%;margin-left: -13px;" src="'+ NP_config.loadingImg +'"/></div>');
            $('body').append(loading) ;
        }
        if(isLoading){
            loading.show();
        }else{
            loading.hide();
        }
    } ,
    setMemu : function(targetPage){
        var menu = $('#menu');
        if(menu.length == 0 || targetPage.length == 0) return false ;
        //操作菜单的显示与隐藏，-1代表隐藏菜单；第一个菜单为1.第二个为2，以此类推
        var tm = targetPage.data('menu');
        if(!!tm && tm != '-1' ){
            var items = menu.find('.item') ;
            items.removeClass(NP_config.menuShowClass);
            items.removeClass('sec');
            items.each(function(i){
                var sort = $(this).data('sort');
                if(sort == tm){
                    $(this).addClass(NP_config.menuShowClass);
                }
            });
            menu.find('.item').not('.cur').eq(1).addClass('sec');
            menu.show();
        }else{
            menu.hide();
        }
    } ,
    scroll : function(targetPage){
        var pos = targetPage.attr('data-pos');
        if(!pos) pos = 0 ;
        $('body').scrollTop(pos);
    } ,
    loadHtml : function(fileName){
        var url = NP_config.host +  NP_config.pageRoute + "/"  + fileName ;  
        var html = null ;
        $.ajax({
            url: url ,
            async : false ,
            type: "post",
            dataType : 'text' ,
            success : function(result){
                var holder = new RegExp(NP_config.start + 'host' + NP_config.end , "g" );
                var val = NP_config.host ;
                result = result.replace( holder , val );
                html = $(result);
                $('body').prepend(html);
            }
        });
        return html ;
    } ,
    fill : function( baseHtml , data , isPrev){
        var html = baseHtml.toString();
        var holder , val ,  obj ;
        if(!data){
            data = {} ;
        }
        //配置一个全局的变量，项目的根目录
        data['host'] = NP_config.host ;
        for(var key in data){
            holder = new RegExp("N." + key + '.L',"g");
            val = data[key];
            html = html.replace( holder , val );
        }
        obj = $(html);
        NP_component.loadImg(obj, isPrev);
        return obj ;
    } ,
    preloadPage : function(showingPage){
        var preloads = showingPage.attr('data-preload') ;
        if(!preloads) return false ;
        var items = preloads.split(',');
        var fileName , existPage ;
        for(var i = 0 ; i< items.length ; i++){
            fileName = items[i] ;
            existPage = $('#page_' + fileName);
            if(existPage.length == 0){
                NP_component.loadPage(fileName , function(res,pageId) {
                    var page = $(res);
                    page.attr('id','page_' + pageId );
                    $('body').prepend(page);
                });
            }
        }
    } ,
    loadPage : function( pageId , success ) {
        var url = NP_config.host + NP_config.pageRoute + "/page_" + pageId + ".html" ;
        var loader = NP_component.createLoader();
        loader.load( url , function (result , status ) {
            if(status == 'success'){
                var holder = new RegExp( NP_config.start + "host" + NP_config.end ,"g");
                result =  result.replace( holder , NP_config.host)
                    .replace(new RegExp( NP_config.start + "service" + NP_config.end ,"g") , NP_config.service)
                    .replace(new RegExp( NP_config.start + "cid" + NP_config.end ,"g") , NP_DATA.cookieUid);
                if( $.isFunction( success ) ){
                    success(result ,pageId) ;
              }
            }else{
                console.error('[niepErro]：page_' + pageId + ".html不存在" );
            }
        });
        loader.empty();
    } ,
    createLoader : function() {
        var loader = $('#NP_loader_container');
        if(loader.length != 0)  return loader ;
        loader = $('<div id="NP_loader_container" style="display: none;"></div>') ;
        $('body').append(loader) ;
        return loader ;
    },
    loadResources : function( params , targetPage  , drawBoard){
        //加载首页面
        var startPage = NP_DATA.startParams.page ;
        if(!startPage){
            //已经加载过首页面
            NP_component.request( params , targetPage );
        }else{
            //未加载首页面时，应当先加载首页面
            NP_component.loading(true) ;
            NP_component.loadPage(startPage , function (result) {
                var page = $(result);
                page.find('[data-prev="true"]').each(function(){
                    $(this).attr('data-prev',false);
                });
                page.attr('id' , 'page_' + startPage ) ;
                $('body').prepend(page);
                NP_DATA.startParams['page'] = '' ;
                NP_component.request( NP_DATA.startParams.params , page );
            }) ;
        }
    } ,
    request : function( params , target ){
        var clone = target.data('clone');
        if(clone){
            var targetId = target.attr('id');
            var attachTimes = NP_DATA.pageParams[targetId+'_'+params];
            var cloneTarget = $("#" + targetId + "_" + attachTimes) ;
            if(cloneTarget.length == 0 ){
                //如果此页面未被复制出来过，那么复制目标页面
                target = $(target.clone());
                var times =  new Date().getTime() ;
                NP_DATA['pageParams'][targetId+'_'+params] = times ;
                target.attr({
                    'id' : targetId + "_" + times ,
                    'data-clone':'cloneBody' ,
                    'data-pageparams' : params ,
                    'data-ontology' : targetId
                });
                $('body').append(target);
            }else{
                target = cloneTarget ;
            }
        }
        NP_component.title(target);
        target.attr('data-params' , params);
        var html = target.html();
        html = html.replace(new RegExp( NP_config.start + "params" + NP_config.end ,"g") , params);
        target.html($(html));
        //请求数据
        var cmds = target.find('[data-unload="once"],[data-unload="refresh"],[data-unload="static"]') ;
        //请求数据完成后，会显示页面
        NP_component.getData(target, params) ;
        if(cmds.length == 0){
            target.pageShow();
        }
    } ,
    getData : function(targetPage , params ){
        var cmds = targetPage.find('[data-unload="once"],[data-unload="refresh"],[data-unload="static"]') ;
        targetPage.attr('data-count',cmds.length);
        cmds.each(function(){
            var ts = $(this);
            var cmd = eval(ts.data('cmd'));
            new cmd( ts , decodeURI(params) , targetPage);
            //关闭页面数据加载
            if( ts.data('unload') == 'once'){
                ts.attr('data-unload',false);
            }else if( ts.data('unload') == 'static'){
                ts.attr('data-unload','loaded');
            }
        });
    } ,
    loadImg : function(ts , isPrev){
        //此方法用于在页面进行切换时候，加载提前预置好的图片资源；此时可能图片已加载过了默认的图片
        ts.find('img').each(function(){
            var cur = $(this);
            var defUrl = cur.data('default') ;
            if(isPrev == true ){
                if(!!defUrl){
                    cur.attr('src',defUrl);
                }
            }else{
                var loading = cur.data('loading') ;
                if(!!loading && loading != 'null'){
                    cur.attr('src' , loading);
                }else if(!!defUrl){
                    cur.attr('src',defUrl);
                }
            }
        });
    } ,
    component : [] ,
    setComponent : function(ele,html){
        var params = !!ele.data('params') ? ele.data('params') : '' ;
        NP_component.component[ele.data('cmd') + params] = html ;
    } ,
    getComponent : function(ele){
        var params = !!ele.data('params') ? ele.data('params') : '' ;
        var keys = ele.data('cmd') + params ;
        var jsons = NP_component.component ;
        for(key in jsons){
            if(key == keys){
                return jsons[key];
            }
        }
    } ,
    getEleHtml : function(ele){
        var parent = $('<div></div>');
        parent.append(ele.clone());
        return parent.html();
    } ,


    backUrl : '' ,
    login : function(loginPageId){
        if(!loginPageId){
            loginPageId = NP_config.loginPageId ;
        }
        var curAddr = NP_component.backUrl ;
        if(!curAddr){
            NP_component['backUrl'] = window.location.href ;
            curAddr = NP_component.backUrl ;
        }
        NP_component.switchPage({target : '#' + loginPageId , params: curAddr });
    } ,
    switchPage : function(params){
        if(!params){
            console.error('switchPage(params)中params参数缺失');
        }else if( !('target' in params)){
            console.error('switchPage(params)中target参数缺失');
        }
        var target = params.target ;
        var switchBtn = $('<a href="" data-action="switch" data-params="" style="display:none">switchPage</a>');
        switchBtn.attr('href' , target);
        switchBtn.attr('data-params' , params.params);
        var showPage = Niep.getShowPage();
        if(showPage.length == 0){
            showPage = $('body');
        }
        showPage.append(switchBtn);
        switchBtn.click();
    } ,
    small : function(tips , time , isTop){
        var name = isTop ? 'top' : 'bottom' ;
        var tipBtn = $("<div style='width:100% ; position:fixed ;text-align:center; " + name + ":20%;left:0;' ><span style='background:#000;color:#fff;padding:10px 15px ; border-radius:4px'>"+tips+"</span></div>");
        $('body').append(tipBtn);
        if(!time) time = 3000 ;
        tipBtn.fadeOut(time);
    } ,
    reloadPage : function(pages){
        for(var i = 0 ; i < pages.length ; i ++ ){
            var ts = $('#page_' + pages[i]).find('[data-unload="false"]');
            ts.attr('data-unload','once').attr('pageNum',1);
        }
    } ,
    refreshCmd : function(cmd) {
       $('[data-cmd="'+cmd+'"]').each(function () {
           if($(this).attr('data-unload') == 'false'){
               $(this).attr('data-unload','once');
           }
       }) ;
    } ,
    title : function(target){
        var title = target.data('title');
        if(!!title){
            document.title = title ;
        }else{
            document.title = NP_config.title ;
        }
    } ,
    getShowPage : function() {
        return $('.' + NP_config.showClass + "." + NP_config.baseClass ) ;
    }

} ;




//历史记录组件
var NP_history = {
    list : [] ,
    push : function(historyPage , targetPageId , params){
        var id = historyPage.attr('id');
        var list = NP_history.list ;
        var last = list[list.length - 1] ;
        if(list.length == 0 || id != last.id){
            targetPageId = "page=" +  targetPageId.split('_')[1];
            if(!!params){
                targetPageId = targetPageId + "&params=" + params ;
            }
            if(!!NP_DATA.historyAction){
                targetPageId = NP_DATA.historyAction + "?" +  targetPageId ;
            }
            //存储历史记录
            history.pushState( id , id , targetPageId );
            list.push({
                id : id ,
                html : historyPage.html()
            });
        }
    } ,
    back : function(){
        var list = NP_history.list ;
        if(list.length > 0 ){
            //删除并返回最后一个记录
            var target = list.pop();
            Niep.getShowPage().niepHide();
            var historyPage = $('#'+target.id);
            NP_component.loading(false);
            historyPage.html(target.html).niepShow();
            NP_component.scroll(historyPage);
            NP_component.setMemu(historyPage);
            NP_component.title(historyPage);
        }
    }
} ;

(function($){
    $.fn.pageShow = function(){
        var obj = this;
        if($.isFunction(NP_config.beforePageShow)){
            NP_config.beforePageShow(obj);
        }
        //加载静态图片资源
        obj.loadStaticImg();
        NP_component.setMemu(obj);
        NP_component.loading(false);
        $('#loading').fadeOut();
        //关闭所有的显示页面
        $('.' + NP_config.baseClass).removeClass('.' + NP_config.showClass);
        obj.niepShow();
        NP_component.scroll(obj);
        NP_component.preloadPage(obj);
        if($.isFunction(NP_config.afterPageShow)){
            NP_config.afterPageShow(obj);
        }
    };
    $.fn.pageHide = function(targetPageId , params){
        var obj = this;
        //存入历史记录
        NP_history.push(obj , targetPageId , params );
        //获取文档的偏移量
        var pos = document.body.scrollTop;
        obj.attr('data-pos' , pos );
        //将page重置
        obj.find('[data-unload="refresh"]').each(function(){
            $(this).attr('pageNum','1');
        });
        obj.niepHide();
        NP_component.loading(true);

    } ;
    $.fn.loadStaticImg = function(isPrev){
        var obj = this;
        if(isPrev) return false ;
        obj.find('img').each(function(){
            var ts = $(this);
            var statics = ts.data('static');
            if(!!statics){
                ts.attr('src',statics);
            }
        });
    };
    $.fn.pageList = function( datas , tips ){
        var ele = this ;
        var tips = datas.emptyTip ;
        if(!tips) tips = 'Sorry,暂无数据哦~' ;
        var list = datas.page.list ;

        var baseObj ;
        var modal = ele.children().eq(0);
        var modalHtml = NP_component.getEleHtml(modal) ;
        var prevLoad = ele.data('prevLoad');
        var component = ele.attr('data-component-refresh-loaded');
        var unloadType = ele.data('unload');
        //页码数据
        var pageNum = datas.page.pageNumber ;
        var totalPage = datas.page.totalPage ;
        if(!component){
            ele.attr('data-component-refresh-loaded' , true );
            NP_component.setComponent(ele, modalHtml);
            ele.empty();
        }else{
            if( pageNum == 1){
                ele.empty();
            }
            modalHtml = NP_component.getComponent(ele) ;
        }
        //如果list为空，则停止
        if(list.length == 0){
            ele.html('<div style="text-align:center;font-size:12px;color:#bbb;">' + tips + '</div>').css('padding','20px');
            return ;
        }
        var moreBtn = ele.find('.getMoreDataBtn');
        if( moreBtn.length == 0){
            moreBtn = $('<a href="javascript:;" class="getMoreDataBtn">加载更多…</a>');
            ele.append(moreBtn);
        }

        var allObj = [] ;

        for(var i in list){
            baseObj = NP_component.fill(modalHtml, list[i], prevLoad);
            baseObj.find('if').each(function () {
               var key = $(this).data('key');
               if( list[i][key] != $(this).data('value') ){
                   $(this).remove();
               }else{
                   $(this).next('else').remove();
               }
            });
            if(!!datas.deal && typeof datas.deal != 'undefined' || datas.deal != undefined){
                datas.deal(baseObj , i);
            }
            allObj.push(baseObj.prop('outerHTML'));
        }

        if(datas.cutMore == true){
            ele.html(allObj.join(''));
        }else{
            moreBtn.before(allObj.join(''));
        }

        if(pageNum == totalPage || datas.cutMore == true ){
            moreBtn.remove();
            if(datas.cutMore == true){
                if(pageNum == totalPage){
                    ele.attr('pageNum' , 1);
                }else{
                    ele.attr('pageNum' , pageNum + 1);
                }
            }
        }else if( pageNum != totalPage && ( !prevLoad || ( prevLoad && (unloadType == 'once' || unloadType == 'false' ) )) ){
            ele.attr('pageNum' , pageNum + 1);
        }

    };

    $.fn.list = function( datas ){
        var ele = this ;
        var tips = datas.emptyTip ;
        var list = datas.list ;
        if(!tips) tips = 'Sorry,暂无数据哦~' ;
        var baseObj ;
        var modal = ele.children().eq(0);
        var modalHtml = NP_component.getEleHtml(modal) ;
        var prevLoad = ele.data('prevLoad');
        var component = ele.attr('data-component-refresh-loaded');
        if(!component){
            ele.attr('data-component-refresh-loaded' , true );
            NP_component.setComponent(ele, modalHtml);
            ele.empty();
        }else{
            ele.empty();
            modalHtml = NP_component.getComponent(ele);
        }
        if(list.length == 0){
            ele.html('<div style="text-align:center;font-size:12px;color:#333;">' + tips + '</div>').css('padding','20px');
            return ;
        }
        for(var i in list){
            baseObj = NP_component.fill(modalHtml, list[i], prevLoad);
            if(!!datas.deal && typeof datas.deal != 'undefined' || datas.deal != undefined){
                datas.deal(baseObj , i );
            }
            ele.append(baseObj);
        }

    };
    $.fn.fill = function( datas , deal){
        var ele = this ;
        var objects = {};
        if(!!datas){
            objects = datas ;
        }
        objects['host'] = NP_config.host ;
        var html = ele.html();

        if(!html) return false ;
        var component = ele.attr('data-component-refresh-loaded');
        if(!component){
            ele.attr('data-component-refresh-loaded' , true );
            //预存组件
            NP_component.setComponent(ele, html);
        }else{
            html = NP_component.getComponent(ele);
        }

        var holder , val ;
        for(var key in objects){
            holder = new RegExp(NP_config.start + key + NP_config.end , "g" );
            val = objects[key] ;
            html = html.replace( holder , val );
        }
        var baseObj = $(html);


        var prevLoad = ele.data('prevLoad');
        NP_component.loadImg(baseObj, prevLoad);
        if(!!deal && typeof deal != 'undefined' || deal != undefined){
            deal(baseObj);
        }
        ele.html(baseObj);

        ele.find('if').each(function () {
            var key = $(this).data('key');
            if( objects[key] != $(this).data('value') ){
                $(this).remove();
            }else{
                $(this).next('else').remove();
                $($(this).html()).insertAfter($(this));
                $(this).remove();
            }
        });

    };

    //填充数据
    $.fn.fillData = function(data , deal){
        var ele = this ;
        var className , obj ;
        for(var key in data){
            className = 'data_' + key ;
            obj = ele.find('input.' + className + ' , select.' + className + ' , textarea.' + className + 'select.' + className);
            if(obj.length != 0){
                obj.val(data[key]);
            }else{
                obj = ele.find('img.' + className);
                if(obj.length != 0){
                    var url = data[key];
                    obj.attr('data-loading',url);
                }else{
                    obj = ele.find('.' + className);
                    if(obj.length != 0){
                        obj.text(data[key]);
                    }
                }
            }
        }
        var prevLoad = ele.data('prevLoad');
        NP_component.loadImg(ele, prevLoad);
        if(!!deal && typeof deal != 'undefined' || deal != undefined){
            deal();
        }
    };

    $.fn.getBaseObj = function(){
        var baseHtml = $('#' + this.data('cmd') ).html();
        return $(baseHtml);
    };

    $.fn.getObj = function(){
        var ele = this ;
        var baseHtml = ele.html();
        var component = ele.attr('data-component-refresh-loaded');
        if(!component){
            ele.attr('data-component-refresh-loaded' , true );
            //预存组件
            NP_component.setComponent(ele , baseHtml);
        }else{
            baseHtml = NP_component.getComponent(ele);
        }

        return $(baseHtml) ;
    };

    //单页显示
    $.fn.niepShow = function(){
        this.addClass(NP_config.showClass);
    } ;

    //单页隐藏
    $.fn.niepHide = function(){
        this.removeClass(NP_config.showClass);
    } ;

    //数据加载完成
    $.fn.finished = function(){
        var component = this ;
        var page = component.parents( "." + NP_config.baseClass);
        var count = page.attr('data-count');
        if(!count){
            count = 0 ;
        }else{
            count = count - 1 ;
        }
        page.attr( 'data-count' , count );
        var prevLoad = component.data('prevLoad');
        if(count == 0 && prevLoad != 'need'){
            page.pageShow();
        }else if(prevLoad == 'need'){
            component.data( 'prevLoad' , 'completed');
        }
        var clone = page.data('clone');
        if( clone == 'cloneBody' && component.attr('data-unload') == 'loaded' ){
            var ontology = $('#' + page.attr('data-ontology')).find('[data-unload="static"]');
            ontology.each(function() {
              if($(this).data('cmd') == component.data('cmd') ){
                  $(this).after($(component.clone())) ;
                  $(this).remove();
              }
            });
        }
    };

    //ajax数据加载完成
    $.fn.ajaxFinished = function(result , success){
        var component = this ;
        var status = result.status ;
        if(status == '200'){ //请求成功
            success(result);
            component.finished();
        }else if(status == '800'){ //付费
            NP_component.refreshCmd('page_chapter');
            NP_component.switchPage({target:'needPay' , params : result.message }) ;
        }else if(status == '100'){ //微信授权
            NP_component.refreshCmd('info_user');
            var page = result.message ;
            var random = Math.random(9000)+1000;
            location.href = NP_config.host +　"/getCode?page="+ page +"&params=1" + "#" + random ;
        }else{
            alerted(result.message);
        }
    };

    $.fn.post = function( postJson , success){
        var component = this ;
        var url = NP_config.service + "/" + component.data('cmd').replace( new RegExp('_' , 'g') , '/' ) ;
        $.post( url , postJson , function(result){
            success(result) ;
        } ,'json');
    } ;

    $.fn.pagePost = function( postJson , success){
        var component = this ;
        var jsons = {} ;
        if(!jQuery.isFunction(postJson)){
            jsons = !!postJson ? postJson : {} ;
        }
        jsons['pageNum'] = component.attr('pageNum') || 1 ;
        var url = NP_config.service + "/" + component.data('cmd').replace( new RegExp('_' , 'g') , '/' ) ;
        $.post( url , jsons , function(result){
            if(jQuery.isFunction(postJson)){
                postJson(result) ;
            }else if(jQuery.isFunction(success)){
                success(result) ;
            }

        } ,'json');
    } ;
})(jQuery);

//读取历史记录
window.onpopstate = function(ev){
    NP_history.back();
};

//加载更多数据
$('body').on('click','.getMoreDataBtn',function(){
    var ts = $(this);
    var parent = ts.parent();
    var page = ts.parents('.' + NP_config.baseClass);
    var params = page.attr('data-params');
    var cmd = eval(parent.data('cmd'));
    new cmd(parent , params , page);
});


//页面切换
$('body').on('click','[data-action="switch"] , [data-action="search"]',function(e){
    e.preventDefault();
    var showingPage = Niep.getShowPage();
    var ts = $(this);
    if(ts.data('action') == 'search'){
        var name = ts.data('name') ;
        var linkInp = showingPage.find('input[name="'+ name +'"]') ;
        ts.attr('data-params' , name + ":" + linkInp.val() ) ;
    }
    var cmd = ts.data('cmd') ;
    if(!!cmd){
        var cmds = eval(cmd);
        new cmds(ts);
    }
    var params = ts.attr('data-params');
    var tId = "page_" + ts.attr('href');
    if(showingPage.length != 0){
        showingPage.pageHide( tId , params);
    }else{
        //存入历史记录
        var targetPageId = tId ;
        targetPageId = "page=" + targetPageId.split('_')[1];
        if(!!params){
            targetPageId = targetPageId + "&params=" + params ;
        }
        if(!!NP_DATA.historyAction){
            targetPageId = NP_DATA.historyAction + "?" +  targetPageId ;
        }
        //存储历史记录
        history.pushState( '' , '' , targetPageId );
    }
    NP_component.loading(true);
    //params = decodeURI(params);

    var target = $('#' + tId);
    if(target.length == 0){
        NP_component.loadPage( ts.attr('href') , function (result) {
            target = $(result);
            target.attr('id' , tId);
            $('body').prepend(target);
            NP_component.loadResources(params, target , ts);
        }) ;
    }else{
        NP_component.loadResources(params, target , ts);
    }
});
