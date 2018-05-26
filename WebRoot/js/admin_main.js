var w = $(window).width();
var h = $(window).height();
function valideLogin(res , btn) {
    $('[data-reset="true"]').click();
}

var resetLink = $('[data-reset="link"]') ;
var resetPswLink = resetLink.attr('href');
function resetLogin() {
   if(w < 768){
       var h = - ($('.login-wrapper').height())/2;
       $('.login-wrapper').css('margin-top' , h + 'px');
   }
}

$('[data-cmd="resetLink"]').on('keyup',function(){
    resetLink.attr('href' , resetPswLink + "?uName=" + $(this).val());
});

$(function () {
   resetLogin();
});

//显示弹层
$('[data-toggle="modal"]').on('click',function(){
   var ts = $(this) ;
   $(ts.attr('href')).find('[data-send="true"]').attr('href', ts.data('url'));
});

$('[data-cmd="submitForm"]').on('click',function(){
   $(this).parents('form').submit();
});

function countNumber(jsons) {
   jsons['e.count'] = ue.getContentTxt().replace(new RegExp(" ","g") , '').length;
}


var fix = $('.panel-fixed');
var fixW = fix.innerWidth();
$(window).on('scroll',function(){
   var top = $(window).scrollTop();
    if(top > 100){
        $('.panel-fixed').css({
           'position':'fixed',
            'top':'0',
            'width':fixW+'px'
        });
    }else{
        $('.panel-fixed').css({
            'position':'static',
            'top':'auto'
        });
    }
});

function needContinue(res,btn) {
   if(confirm("请问需要继续发布章节吗？")){
       window.location.href = host + "/admin/info/chapter?novelId=" + btn.data('id') ;
   } else{
       window.location.href = host + "/admin/list/chapter?novelId=" + btn.data('id') + "&isAdd=1" ;
   }
}

function saveNovelSuccess(res,btn) {
    window.location.href = host + "/admin/info/chapter?novelId=" + res.message ;
}

function pushChapterMsgSuccess(res,btn) {
    var status = res.status ;
    if(status == '200'){
        alerted(res.message) ;
    }else if(confirm(res.message)){
        location.reload() ;
    }
}

var finishedCount = 0 ;
function saveConfigSuccess(res,btn) {
    if(finishedCount > 0 ){
        finishedCount = 0 ;
        alerted("保存成功");
    }else{
        finishedCount ++ ;
    }
}