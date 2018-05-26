function alerted(text){
    alert(text) ;
}
var host = $('#host').val();
var mReg = /^1[3|4|5|8|7][0-9]\d{8}$/ ;
var pReg = /^[1-9]{1}[0-9]*([.]{1}[0-9]{1,2})?$/;

function checkData(ele){
    var eVal = ele.val();
    var itemName = ele.parents('.form-group').find('label').text().replace('：','').replace('请输入' ,'');
    if(!itemName){
        itemName = ele.attr('placeholder');
        if(!!itemName){
            itemName = itemName.replace('：','').replace('请输入' ,'');
        }
    }
    var isEmpty =  ele.attr('data-empty') ; //如果可为空，则为true ; 如果为某个值时视为空，则传入此值
    if( (!isEmpty || isEmpty == 'false' ) && !eVal   ){
        alert( itemName + "不能为空");
        return false ;
    }

    if(!!isEmpty && eVal == isEmpty){
        alert( "请选择" + itemName);
        return false ;
    }

    var minLen = ele.data('min');
    var maxLen = ele.data('max');
    if(!isEmpty){
        if((!!minLen && eVal.length) < minLen || (!!maxLen && eVal.length > maxLen) ){
            var text = itemName + "的长度为" + minLen + "~" + maxLen + "个字符" ;
            if(minLen == maxLen) text = itemName + "输入错误" ;
            if(!minLen) text = itemName + "的最大长度为" + maxLen + "个字符" ;
            if(!maxLen) text = itemName + "的最小长度为" + minLen + "个字符" ;
            alert( text);
            return false ;
        }

        var type = ele.data('type');
        if(isEmpty != true || !!eVal){
            if( (type == 'mobile' )&& !mReg.test(eVal)){
                alert( itemName + "不正确" );
                return false ;
            }
            if( type == 'amount' && !pReg.test(eVal)){
                alert( itemName + "不正确");
                return false ;
            }
        }
    }
    return true ;
}

/*
 * 使用场景：ajax提交表单，为a添加data-submit="true"属性
 * 将所有需要提交的表单添加name，会自动收集以name为key的json数据
 * 默认数据保存成功后，刷新当前页面，如果需要跳转至其他页面，请为a添加data-redirect=""的路径，请忽略host
 */
var formResult = true ;
$('body').on('click', 'a[data-submit="true"]' , function(eve){
    eve.preventDefault();
    var _this = $(this);
    var $btn = $(this).button('loading') ;

    var form = _this.parents('form') ;

    var postJson ={} ;
    form.find(' select , input , textarea ').not('input[type="file"],input[type="checkbox"],input[type="radio"]').each(function(i,e){
        var obj = $(this);
        if(!checkData(obj)){
            formResult = false ;
            return false ;
        }else{
            formResult = true ;
        }
        var name = obj.attr('name') ;
        if(!!name) postJson[name] = obj.val();
    }) ;

    //增加额外验证
    var addCheck = _this.data('check') ;
    if(!!addCheck){
        var addCheckFn = eval(addCheck);
        new addCheckFn(_this);
    }
    if(!formResult){
        $btn.button('reset') ;
        return false ;
    }

    var vals = [] ;
    form.find('input[type="checkbox"]:checked').each(function(){
        var obj = $(this);
        vals.push(obj.val());
        var name = obj.attr('name') ;
        if(!!name) postJson[name] = vals.join(',');
    });

    form.find('input[type="radio"]:checked').each(function(){
        var obj = $(this);
        var name = obj.attr('name') ;
        if(!!name) postJson[name] = obj.val();
    }) ;

    //存储其他的值
    var insert = _this.data('insert');
    if(!!insert){
        var insertFn = eval(insert);
        new insertFn( postJson , _this );
    }
    var url = host + _this.attr('href');
    var callBack = _this.attr('data-success');
    var redirect = _this.attr('data-redirect') ;
    var error = _this.attr('data-error') ;
    $.post(url , postJson , function(result){
        $btn.button('reset') ;
        var msg = result.status ;
        var tip = result.message ;
        if(msg == "200"){
            if(!!callBack){
                var fn = eval(callBack);
                new fn( result , _this );
            }else if(!!redirect){
                window.location.href = host + redirect ;
            }else{
                location.reload();
            }
        }else{
            var er = eval(error);
            new er( result , _this );
            alert(tip);
        }
    },'json');
});

$('body').on('click', 'a[data-send="true"]' , function(eve){
    eve.preventDefault();
    var _this = $(this);
    var $btn = $(this).button('loading') ;
    var url = host + _this.attr('href');
    var callBack = _this.attr('data-success');
    var redirect = _this.attr('data-redirect') ;
    $.post(url ,  function(result){
        $btn.button('reset') ;
        var msg = result.status ;
        var tip = result.message ;
        if(msg == "200"){
            if(!!callBack){
                var fn = eval(callBack);
                new fn(_this , result);
            }else if(!!redirect){
                window.location.href = host + redirect ;
            }else{
                location.reload();
            }
        }else if(msg == "100"){
            alert(tip);
        }else{
            alert(tip);
        }
    },'json');
});

function pageSubmit(page) {
    $("#pageNum").val(page);
    $("#myform").submit();
}

$('select').on('change' , function(){
    if($(this).hasClass('notAuto')){
        return false ;
    }
    $("#myform").submit();
});

$('table').each(function(){
    var ts = $(this) ;
    var tr = ts.find('tbody tr');
    if(tr.length == 0){
        var col = ts.find('thead tr th').length ;
        ts.find('tbody').append('<tr><td class="text-muted text-center" colspan="'+col+'">Sorry,暂无数据哦~</td></tr>');
    }
});

$('input[data-type="int"]').on('keyup',function(){
    var ts = $(this) ;
    var val = ts.val();
    if(val.indexOf('.') != -1){
        val = val.replace('.' , ' ') ;
        ts.val(val);
    }
});