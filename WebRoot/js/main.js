console.log('加载main.js');

var w = $(window).width();
var h = $(window).height();
$('body').css('min-height',h+'px');

$('body').on('click', '[data-cmd="resetFont"]' ,function(){
   var ts = $(this) ;
   var fontSize = parseInt(getCookie("fontSize")) || 14 ;
    var type = ts.data('type');
    if(type == '1'){
        fontSize += 2 ;
    }else{
        fontSize -= 2 ;
    }
   if(fontSize >= 28 || fontSize < 12) return false ;
   $('.chapter-content').css({
       'font-size' : fontSize + 'px'
   }) ;
   setCookie("fontSize",fontSize,30);
});

$('body').on('click', '[data-cmd="resetColor"]' ,function(){
    var ts = $(this) ;
    var index = parseInt(getCookie("bagPic")) || 0 ;
    index ++ ;
    if(index == 6){
        index = 0 ;
    }
    $('.colorBlock').css('background-image','url(../img/bag'+index+'.png)') ;
    $('body').attr('class' , 'chapterInfo bag' + index);
    setCookie("bagPic",index,30);
});

//重置加入书架按钮
function resetBtn(btn , result) {
    var collect = btn.parents('.page').find('.collect');
    var times = parseInt(collect.attr('data-times'));
    var type = btn.attr('data-type') ;
    if(type == '1'){
        btn.attr({'href' : '/delete/history?id=' + result.message , 'data-type' : '0'}).text('已加入书架');
        NP_component.small("已加入书架");
        times ++ ;
        collect.attr('data-times' , times).text(times);
    }else{
        btn.attr({'href':'/save/history?isFavorite=true&novelId=' + result.message , 'data-type' : '1'}).text('加入书架');
        NP_component.small("已从书架移除");
        if(times > 0 ){
            times -- ;
            collect.attr('data-times' , times).text(times);
        }
    }
    NP_component.refreshCmd('page_history');
}



$('body').on('click','.backTop',function(){
    $('body,html').animate({scrollTop:0} , 400);
});

$('body').on('click',function(){
    $('.searchList').hide();
});

$('body').on('click','[data-cmd="getNovelList"]',function(){
    var prev = $(this).prev().find('.linkGroup');
    page_novel(prev);
});

function searchChapter(obj) {
    var ts = $(obj) ;
    var title = ts.val();
    var id = ts.data('id') ;
    var searchList = ts.parent().next() ;
    if(!!title){
        var url = NP_config.host + "/page/chapterByTitle" ;
        $.post( url , { title:title , novelId : id } , function (result) {
            var status = result.status ;
            if(status == '200'){
                var page = result.page ;
                var totalRow = page.list.length ;
                if(totalRow > 0){
                    var html = [] ;
                    for (var i = 0; i < page.list.length; i++) {
                        var obj = page.list[i];
                        html.push('<a href="chapterInfo" data-action="switch" ');
                        html.push(' data-params="'+ obj.number +'-'+ obj.novelId +'" class="item">第' + obj.number + '章 ' + obj.title +'</a>');
                    }
                    searchList.html(html.join('')).show();
                }else{
                    searchList.empty().hide();
                }
            }else{
                searchList.empty().hide();
            }
        });
    }else{
        searchList.empty().hide();
    }
}

$('body').on('click','[data-cmd="toSearchChapter"]',function(){
    var ts = $(this).prev() ;
    var title = ts.val();
    var id = ts.data('id') ;
    var searchList = ts.parent().next() ;
    if(!!title){
        var url = NP_config.host + "/page/chapterByTitle" ;
        $.post( url , { title:title , novelId : id } , function (result) {
            var status = result.status ;
            if(status == '200'){
                var page = result.page ;
                var totalRow = page.list.length ;
                if(totalRow > 0){
                    var html = [] ;
                    for (var i = 0; i < page.list.length; i++) {
                        var obj = page.list[i];
                        html.push('<a href="chapterInfo" data-action="switch" ');
                        html.push(' data-params="'+ obj.number +'-'+ obj.novelId +'" class="item">第' + obj.number + '章 ' + obj.title +'</a>');
                    }
                    searchList.html(html.join('')).show();
                }else{
                    searchList.empty().hide();
                }
            }else{
                searchList.empty().hide();
            }
        });
    }else{
        searchList.empty().hide();
    }
});

$('body').on('click', '[data-cmd="showSurprise"]' ,function(){
    var arrs = [] ;
    arrs.push('<div class="givings-wrapper">');
    arrs.push('<div class="giving">');
    arrs.push('<div class="userPhoto"></div><div class="userPhoto-sec">100书币</div>');
    arrs.push('</div>');
    arrs.push('<div class="options"><div>恭喜获得一个新手礼包，是否立即领取？</div>');
    arrs.push('<a href="/update/gift" data-send="true" data-success="newUserSuccess" class="pickBtn pickBtn-ins">立即领取</a>');
    arrs.push('</div>');
    arrs.push('</div>');
    $('body').append(arrs.join('')) ;
});

var timer = null ;
var flag = true ;
$(window).on('scroll',function(){
   var top = $(window).scrollTop() ;

    if(top > 100){
        $('.backTop').fadeIn();
        clearInterval(timer);
        timer = setTimeout(function () {
            $('.backTop').hide();
        },3000);
    }else{
        $('.backTop').fadeOut();
    }
    //自动加载数据
    var bottomDis = $(document).height() - $(window).height() - top ;
    var showPage = NP_component.getShowPage() ;
    var getMoreDateBtn = showPage.find('.getMoreDataBtn');
    if(!!getMoreDateBtn && getMoreDateBtn.length > 0){
        var dis = getMoreDateBtn.parent().data('bottom') || 240;
        if(bottomDis < dis+100 && bottomDis > dis - 100 ){
            if(flag){
                flag = false ;
                getMoreDateBtn.click();
                setTimeout(function () {
                    flag = true ;
                },1000);
            }
        }
    }
});


function dealComment(btn,res) {
    NP_component.small('评论成功');
    NP_component.refreshCmd('page_comment');
    NP_component.switchPage({'target':'novelInfo','params':res.message});
}

function signInSuccess(btn,res) {
    NP_component.small(res.message);
    NP_component.refreshCmd('info_signIn');
    NP_component.switchPage({'target':'signIn'});
}

function exchangeCoinsSuccess(btn,res) {
    NP_component.small(res.message);
    NP_component.refreshCmd('info_user');
    NP_component.refreshCmd('info_signIn');
    NP_component.switchPage({'target':'userCenter'});
}

function buyRecordSuccess(btn,res) {
    NP_component.small(res.message);
    NP_component.refreshCmd('info_user');
    NP_component.switchPage({'target':'chapterInfo' , params : btn.attr('data-params')});
}

function givingsSuccess(btn,res) {
    NP_component.refreshCmd('info_user');
    if(!!res.message){
        NP_component.small(res.message);
    }
    $('.givings-wrapper').remove();
}

function newUserSuccess(btn,res) {
    NP_component.refreshCmd('info_user');
    if(!!res.message){
        NP_component.small(res.message);
    }
    $('.givings-wrapper , .gift').remove();
    NP_component.switchPage({target:'userCenter'}) ;
}

function changeOpenSuccess(btn,res) {
    NP_component.small(res.message) ;
    var inp = btn.prev();
    if(inp.prop('checked')){
        inp.prop('checked',false) ;
    }else{
        inp.prop('checked',true) ;
    }

}

function createSuccess(btn,res) {
    NP_component.loading(true);
    var id = res.id ;
    var payType = res.payType ;
    //window.location.href = NP_config.host + "/update?id=" + id ;
    if(payType == '1'){
        window.location.href = NP_config.host + "/alipay?id=" + id ;
    }else if(payType == '2'){
        window.location.href = NP_config.host + "/pay/pre?id=" + id ;
    }else if(payType == '3'){
        window.location.href = NP_config.host + "/update?id=" + id ;
    }
}

$(function () {
    var timer = null ;
    timer = setInterval(function () {
        if(!Niep){

        }else{
            Niep.init({
                defaultPage : 'home' ,
                beforePageShow : function(page) {
                    var id = page.attr('id');
                    if(!!id && id.indexOf('page_chapterInfo') != -1 ){
                        $('body').addClass('chapterInfo');
                    }else{
                        $('body').removeClass('chapterInfo');
                    }
                    
                    if(!!id && id.indexOf('page_home') != -1 ){
                        page.attr('data-title', $('body').attr('data-homeTitle') ) ;
                        document.title = $('body').attr('data-homeTitle') ;
                    }
                    
                } ,
                afterPageShow : function (page) {
                    //保存pv记录
                    $.post(NP_config.host + "/save/pv");
                }
            });
            clearInterval(timer) ;
        }
    },1);
});


function resetParams(ts) {
   var inp = ts.parents('.single-line-form').find('input').val();
   ts.attr('data-params' , '10-99-99-99-' + inp ) ;
}

function collectParams(ts) {
   var params = ts.attr('data-base');
   ts.parents('.line-novel').siblings().find('a.cur').each(function() {
       var curParam = $(this).attr('data-base');
       if(!!params && !!curParam){
           params += '~~';
       }
       params += curParam ;
   });
   ts.attr('data-params', params);
}

