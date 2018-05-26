/*****************public********************/
function changeNum(num) {
    var arr = ['零' , '一' , '二' , '三' , '四' , '五' , '六' , '七' , '八' , '九' , '十'] ;
    var t = (num+'').split('');
    if(num < 10){
        return arr[num] ;
    }else if(num < 100){
        if(t[1] == 0){
            return arr[t[0]] + '十' ;
        }
        return arr[t[0]] + '十' + arr[t[1]] ;
    }else if(num < 1000){
        if(t[2] == 0){
            if(t[1] == 0){
                return arr[t[0]] + '百' ;
            }
            return arr[t[0]] + '百' + arr[t[1]] + '十' ;
        }else{
            if(t[1] == 0){
                return arr[t[0]] + '百零' + arr[t[2]] ;
            }
        }
        return arr[t[0]] + '百' + arr[t[1]] + '十' + arr[t[2]] ;
    }else{
        return num ;
    }
}

function getJsons(str , keys) {
    str = str+'';
    var json = {} ;
    if(!str){
        return json ;
    }else if(str.indexOf('-') == -1){
        json[keys[0]] = str ;
    }else{
        var arr = str.split('-') ;
        for (var i = 0; i < arr.length; i++) {
            json[keys[i]] = arr[i] ;
        }
    }
    return json ;
}

function getTotalCount(count) {
    var counts = "" + count ;
    if (count < 99999) {
        return counts ;
    }else{
        return counts.substr(0,counts.length - 4) + "万" ;
    }
}

function changeParamsToJson(params){
    var paramsJson= {} ;
    if(!params){
        return paramsJson ;
    }
    params.replace('?','');
    var arr = params.split("~~") ;
    for( var i = 0 ; i< arr.length ; i++){
        var pairs = arr[i] ;
        var pos = pairs.indexOf(':');
        if( pos == -1 )   continue;
        paramsJson[ pairs.substring(0,pos) ] = decodeURI( pairs.substring(pos+1) );
    }
    return paramsJson ;
}

function page_novel( ts , params , page) {
    var json ;
    //if( !!params && params.indexOf(':') > -1){
    //    isCondition = true ;
    //    json = changeParamsToJson(params) ;
    //}else{
    //    json = getJsons( ts.data('params')  , ['pageSize','isRecommend','orderType','isEnd','nName','classifyId','count']);
    //}
    params = ts.attr('data-params') || params ;
    json = changeParamsToJson(params) ;
    ts.pagePost( json , function (result) {
        ts.ajaxFinished(result , function () {
            var cut = ts.data('cut');
            for (var i = 0; i < result.page.list.length; i++) {
                var obj = result.page.list[i];
                obj['totalCount'] = getTotalCount(obj.count);
            }
            result['deal'] = function (obj,i) {
                if(i == 0){
                    obj.find('.column-1').remove();
                }else if(cut == 'recom'){
                    obj.find('.column-2').remove();
                }
            } ;
            if(!!cut){
                result['cutMore'] = true ;
            }
            result['emptyTip'] = 'Sorry，暂时还没有相关小说哦~';
            ts.pageList(result);

            var nName = json.nName ;
            if(!!nName){
                page.find('input[name="nName"]').val(nName);
            }

            page.find('.line-novel a.cur').removeClass('cur');
            page.find('.line-novel a').each(function() {
                var ts = $(this) ;
                if(params.indexOf(ts.attr('data-base')) > -1){
                    ts.addClass('cur');
                }
            });
        });
    }) ;
}

function info_novel(ts,params,page) {
    ts.post( {id : params} , function (res) {
        ts.ajaxFinished(res,function() {
            var data = res.data ;
            data['zNum'] = changeNum(data.lChapterNum) ;
            var arr = ['朕觉此书甚好，要赏！','今天心情真不错，赏！','作者帅得不得了，必须赏！'] ;
            var i = parseInt(parseInt(Math.random()*arr.length , 10) );
            data['goodWord'] = arr[i]  ;
            ts.fill(res.data);
            if(data.isEnd){
                ts.find('.is-serialize').remove();
            }else{
                ts.find('.is-finish').remove();
            }

            var history = res.history ;
            if(!!history){
                ts.find('.control-btn a.start').eq(0).attr('data-params', history.chapterNum + '-' + history.novelId ) ;
                if(history.isFavorite){
                    ts.find('.control-btn a.start').eq(1).attr({'href' : '/delete/history?id=' + history.id , 'data-type' : '0'}).text('已加入书架');
                }
            }
            var givings = res.givings ;
            if(!!givings){
                var arrs = [] ;
                arrs.push('<div class="givings-wrapper">');
                arrs.push('<div class="giving">');
                arrs.push('<div class="userPhoto"></div><div class="userPhoto-sec">'+ givings.coins +' 书币</div>');
                arrs.push('</div>');
                arrs.push('<div class="options"><div>多谢客官打赏，这 <span class="text-yellow">'+ givings.coins +'</span> 个书币可是客官掉的？</div>');
                arrs.push('<a href="/update/givings?type=0&novelId='+givings.novelId+'" data-send="true" data-success="givingsSuccess" class="pickBtn">不是我的</a>');
                arrs.push('<a href="/update/givings?type=1&novelId='+givings.novelId+'" data-send="true" data-success="givingsSuccess" class="pickBtn pickBtn-default">收入囊中</a>');
                arrs.push('</div>');
                arrs.push('</div>');
                page.append(arrs.join('')) ;
            }

            //存储点击记录
            $.post( NP_config.host + "/save/readTimes?id=" + params);
        });
    });
}

function list_classify( ts , params , page ) {
    ts.post(  function (res) {
        ts.ajaxFinished(res,function() {
            var list = res.list ;
            if(!!page && page.attr('id').indexOf('page_novelList') > -1 ){
                ts.list(res);
            }else{
                var html = [] ;
                for (var i = 0; i < list.length; i++) {
                    var obj = list[i];
                    if(!obj.parentId){
                        html.push('<div class="panel panel-room"><div class="panel-header"><a href="novelList" class="left" data-action="switch" data-params="10-99-99-99- -'+ obj.id +'"><span class="font book">&#xe60b;</span>'+ obj.cName +'</a></div>');
                        html.push('<div class="panel-body">');
                        for (var j = 0; j < list.length; j++) {
                            var child = list[j];
                            if(child.parentId == obj.id){
                                html.push('<div class="item"><a class="name" href="novelList" data-action="switch" data-params="10-99-99-99- -'+ child.id +'">'+ child.cName +'</a></div>');
                            }
                        }
                        html.push('</div>') ;
                        html.push('</div>') ;
                    }
                }
                ts.html($(html.join('')));
                ts.find('.panel-body').each(function () {
                    if($(this).find('.item').length==0){
                        $(this).remove();
                    }
                });
            }
        });
    });
}

//章节内容专用
function page_chapter(ts,params,page) {
    var json = getJsons(params,['pageNum','novelId','channelId']) ;
    if(!!ts.attr('pageNum')){
        json['pageNum'] = ts.attr('pageNum');
    }
    ts.post( json , function (res) {
        ts.ajaxFinished(res, function() {
            var chapter = res.page.list[0] ;
            var isFirst = res.page.firstPage ;
            var isLast = res.page.lastPage ;
            if(!isFirst){
                chapter['prevNum'] = chapter.number - 1 ;
            }else{
                page.find('.flex-chapter .item').eq(0).addClass('disabled').attr({'data-action':'','href':'javascript:;'});
            }
            if(!isLast){
                chapter['nextNum'] = chapter.number + 1 ;
            }else{
                page.find('.flex-chapter .item').eq(2).addClass('disabled').attr({'data-action':'','href':'javascript:;'});
            }
            chapter['zNum'] = changeNum(chapter.number) ;
            ts.fill(chapter);

            var fz = parseInt(getCookie('fontSize')) || 16 ;
            $('.chapter-content').css({
                'font-size': fz + 'px'
            });
            var index = parseInt(getCookie('bagPic')) || 0 ;
            $('body').attr('class' , 'chapterInfo bag' + index);
            $('.colorBlock').css('background-image','url(../img/bag'+index+'.png)') ;

            var novelName = chapter.nName ;
            page.attr('data-title' , novelName) ;
            document.title = novelName ;

            //存储阅读记录
            $.post(NP_config.host + "/save/history?novelId=" + chapter.novelId + "&chapterNum=" + chapter.number , function() {
                NP_component.refreshCmd('page_history') ;
            });

            //是否显示二维码信息
            var qd = getQueryParamsJson().qd ;
            if(!!qd && qd.indexOf('-') != -1 ){
                var  qdArr = qd.split('-') ;
                if(qdArr.length == 2 && qdArr[1] == '0'){
                    $('html').addClass('noQd');
                }
            }
        });
    });
}

function getQueryParamsJson(){
    var paramsJson= {} ;
    var href = location.href ;
    var queryStr = location.search ;
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

function page_chapters(ts,params,page) {
    var id = page.attr('id') ;
    var json = getJsons(params,['pageNum','novelId','pageSize','orderType']) ;
    if(id.indexOf('page_chapterListDesc') != -1 ){
        json['orderType'] = 1 ;
    }
    if(!!ts.attr('pageNum')){
        json['pageNum'] = ts.attr('pageNum');
    }
    ts.post( json , function (res) {
        ts.ajaxFinished(res, function() {
            if(id.indexOf('page_chapterList') != -1  || id.indexOf('page_chapterListDesc') != -1){
                for (var i = 0; i < res.page.list.length; i++) {
                    var obj = res.page.list[i];
                    obj['zNum'] = changeNum(obj.number) ;
                }
                ts.pageList(res);
            }else{
                var chapter = res.page.list[0] ;
                chapter['time'] = chapter.time.substr(0,16);
                chapter['zNum'] = changeNum(chapter.number) ;
                ts.fill(chapter) ;
            }
        });
    });
}

function info_user(ts,params,page) {
    var para = ts.data('params');

    ts.post( {page : para} , function (res) {
        ts.ajaxFinished(res, function() {
            var data = res.data ;
            var id = page.attr('id');
            if(!data){
                data = {'coins' : 0} ;
            }
            ts.fill(res.data);
            if(!res.data.isNew){
               ts.find('.gift').remove();
            }
            if(!!data.isOpen){
               ts.find('.checkbox input').prop('checked' , true ) ;
            }
        });
    });
}

function page_comment(ts,params,page) {
    var para = ts.data('params') ;
    ts.pagePost( para , function (res) {
        ts.ajaxFinished(res, function() {
            res['emptyTip'] = 'Sorry,暂无评论哦~';
            for (var i = 0; i < res.page.list.length; i++) {
                var obj = res.page.list[i];
                obj['time'] = obj.time.substr(0,16);
            }
            ts.pageList(res);
            page.find('.commentCount').text(res.page.totalRow);
        });
    });
}

function page_recharge(ts,params) {
    ts.pagePost(ts.data('params') , function (res) {
        ts.ajaxFinished( res , function() {
            for (var i = 0; i < res.page.list.length; i++) {
                var obj = res.page.list[i];
                obj['amountY'] = (obj.amount*0.01).toFixed(2) ;
                obj['time'] = (obj.time).substr(0,16);
            }
            res['emptyTip'] = 'Sorry,暂时还没有充值记录哦~' ;
            res['deal'] = function (obj,j) {
               if(res.page.list[j].type == 2){
                    obj.addClass('content-give');
               }
            } ;
            ts.pageList(res);
        });
    });
}
function page_history(ts,params,page) {
    var id = page.attr('id') ;
    var json = id == 'page_collections' ? {isFavorite : 1 } : '' ;
    ts.pagePost(json , function (res) {
        ts.ajaxFinished(res, function() {
            for (var i = 0; i < res.page.list.length; i++) {
                var obj = res.page.list[i];
                obj['zNum'] = changeNum(obj.chapterNum) ;
            }
            ts.pageList(res);
        });
    });
}

function info_signIn(ts,params,page) {
    ts.post(  function (res) {
        ts.ajaxFinished(res, function() {
            var data = res.data || { score : 0 , days : 0 };
            var now = new Date() ;
            var time = new Date(data.time);
            if(!!data.time && ( now.getFullYear() == time.getFullYear() && now.getDate() - time.getDate() > 1 && now.getMonth() == time.getMonth() ) ){
                //没有连续签到
                data['days'] = 0 ;
                data['dScore'] = 1 ;
            }
            data['dScore'] = data.days < 7 ? data.days + 1 : data.days ;

            ts.fill(data);


            if(now.getFullYear() == time.getFullYear() && now.getDate() == time.getDate() && now.getDate() == time.getDate() ){
                page.find('.date').attr({'href':'javascript:;' , 'data-send' : ''}).text('今日已签到').addClass('disabled');
            }
            if(data.score < 10){
                page.find('.getCoins').remove();
            }
        });
    });
}

function info_history(ts,params) {
    ts.post(  function (res) {
        ts.ajaxFinished(res, function() {
            var data = res.data ;
            if(!!data){
                data['zNum'] = changeNum(data.number) ;
                ts.fill(data);
                ts.show();
            }else{
                ts.hide();
            }
        });
    });
}


function checkUserAgent(ts,params,page) {
    var ua = window.navigator.userAgent.toLowerCase();
    if(ua.match(/MicroMessenger/i) == 'micromessenger'){
        ts.children('a').remove();
    }else{
        ts.children('div').remove();
    }
    ts.finished();
}

function page_maxReadTimes(ts,params,page) {
    ts.pagePost( function (res) {
        ts.ajaxFinished(res, function() {
            ts.pageList(res);
        });
    });
}

function page_freeBooks(ts,params,page) {
    ts.pagePost( function (res) {
        ts.ajaxFinished(res, function() {
            ts.pageList(res) ;
        });
    });
}