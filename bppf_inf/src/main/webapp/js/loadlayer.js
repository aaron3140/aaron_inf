/**
 * 加载层脚本:
 * @author lyz
 * 2009-03-18
 */

// 锁定层对象
function LoadLayer(img, divClassName, textClassName, parentObj, text) {
	var p = this.getContextPath();
	// 初始化参数
	this.img = img || p + "/cmpf/images/loadlayer/loading.gif";
	this.divClassName = divClassName || "loadLayerDefault";
	this.textClassName = textClassName || "loadTextDefault";
	this.parentObj = (typeof(parentObj) == "string" ? document.getElementById(parentObj) : parentObj) || document.body;
	this.text = text || "页面加载中...";
	//this.iFramePage = "about:blank";
	this.iFramePage = p + "/cmpf/common/blank.html";
	//
	this.panel = null;
	this.content = null;
	this.id = "";
	this.timeCount = false;
	this.intervalId = -1;
}

/**
 * 设置显示文字
 * @author lyz
 * 2009-07-10
 */
LoadLayer.prototype.setText = function(text) {
	this.text = text;
	//
	var tdt = document.getElementById("_loadLayerText");
	if (tdt !== null) {
		tdt.innerText = text;
	}
};

/**
 * 是否启用计时
 * @author lyz
 * 2010-02-01
 */
LoadLayer.prototype.enableTimeCount = function(timeCount) {
	this.timeCount = timeCount;
};

/**
 * 设置IE低版本浏览器铺设的iframe
 * @author lyz
 * 2009-03-16
 */
LoadLayer.prototype.setIFramePage = function(iFramePage) {
	this.iFramePage = iFramePage;
};

/**
 * 设置层ID
 * @author lyz
 * 2009-06-22
 */
LoadLayer.prototype.setId = function(id) {
	this.id = id;
};

/**
 * 设置要显示位置的父对象
 * @author lyz
 * 2009-03-16
 */
LoadLayer.prototype.setParentObj = function(parentObj) {
	if (typeof(parentObj) == "string") {
		parentObj = parentObj.replace(/(^\s*)|(\s*$)/g, "");
		//
		if (parentObj == "document.body") {
			parentObj = document.body;
		} else {
			this.parentObj = document.getElementById(parentObj);
		}
	} else if (typeof(parentObj) == "object") {
		this.parentObj = parentObj;
	}
	// 重新绘制层
	if (this.parentObj === null) {
		alert("parentObj设置错误");
		return;
	}
	this.init();
};

/**
 * 初始化，必须
 * @author lyz
 * 2009-03-18
 */
LoadLayer.prototype.init = function() {
	// 删除原有层
	var divPanel = document.getElementById(this.id === "" ? "div_loadLayer_panel" : "div_loadLayer_panel" + this.id);
	var divContent = document.getElementById(this.id === "" ?  "div_loadLayer_content" : "div_loadLayer_content" + this.id);
	if (divPanel !== null) {
		divPanel.parentNode.removeChild(divPanel);
	}
	if (divContent !== null) {
		divContent.parentNode.removeChild(divContent);
	}
	// 创建底板
	var panel = this.createPanel(this.parentObj);
	// 创建层
	var content = this.createContent(panel);
	// 嵌套进body
	this.panel = document.body.appendChild(panel);
	this.content = document.body.appendChild(content);
	//
	//alert(this.panel.outerHTML);
	//alert(this.content.outerHTML);
};

/**
 * 创建底板
 * @author lyz
 * 2009-03-18
 */
LoadLayer.prototype.createPanel = function(parentObj) {
	// 获取父对象高宽和坐标
	var pw = parentObj.clientWidth;
	var ph = parentObj.clientHeight;
	var pt = this.getTop(parentObj);
	var pl = this.getLeft(parentObj);
	//alert("pw: " + pw + "\nph: " + ph + "\npt: " + pt + "\npl: " + pl);
	// 创建临时层获取样式高宽
	var tmp = document.createElement("div");
	tmp.className = this.divClassName;
	tmp.style.display = "none";
	document.body.appendChild(tmp); // 必须加入才能获取样式
	// 计算高宽和坐标
	var st = new StyleUtil();
	var styleWidth = this.getStyleWidth(st.getStyle(tmp, "width"), pw);
	var styleHeight = this.getStyleHeight(st.getStyle(tmp, "height"), ph);
	var w = isNaN(styleWidth) ? styleWidth.substring(0, styleWidth.length - 2) : styleWidth;
	var h = isNaN(styleHeight) ? styleHeight.substring(0, styleHeight.length - 2) : styleHeight;
	var t = pt + (ph - h) / 2;
	var l = pl + (pw - w) / 2;
	//alert("w: " + w + "\nh: " + h + "\nt: " + t + "\nl: " + l);
	// 设置底板样式
	var panel = document.createElement("div");
	if (this.id === "") {
		panel.id = "div_loadLayer_panel";
	} else {
		panel.id = "div_loadLayer_panel_" + this.id;
	}
	panel.style.width = styleWidth;
	panel.style.height = styleHeight;
	panel.style.top = t;
	panel.style.left = l;
	panel.style.position = "absolute";
	panel.style.zIndex = 5000;
	panel.style.backgroundColor = "#FFFFFF";
	panel.style.visibility = "hidden";
	// 判断低于IE7则铺一层iFrame
	var iFrameHTML = "";
	var sb = new StringBuffer();
	var userAgent = navigator.userAgent;
	if (userAgent.indexOf("MSIE 5") != -1 || userAgent.indexOf("MSIE 6") != -1) {
		if (this.id === "") {
			sb.append("<iframe id='frame_loadLayer_frame' width='100%' height='100%' align='top'");
		} else {
			sb.append("<iframe id='frame_loadLayer_frame_" + this.id + "' width='100%' height='100%' align='top'");
		}
		sb.append(" frameborder='1' marginheight='0' marginwidth='0' scrolling='no'");
		sb.append(" src='" + this.iFramePage + "'");
		sb.append(" style='width:" + styleWidth + ";height:" + styleHeight + ";");
		sb.append(" filter:progid:DXImageTransform.Microsoft.Alpha(opacity=0)'>");
		sb.append("</iframe>");
		iFrameHTML = sb.toString();
	}
	panel.innerHTML = iFrameHTML;
	//
	return panel;
};

/**
 * 创建层
 * @author lyz
 * 2009-03-18
 */
LoadLayer.prototype.createContent = function(panel) {
	var content = document.createElement("div");
	// 设置层样式
	if (this.id === "") {
		content.id = "div_loadLayer_content";
	} else {
		content.id = "div_loadLayer_content_" + this.id;
	}
	content.className = this.divClassName;
	content.style.position = "absolute";
	content.style.zIndex = panel.style.zIndex * 10;
	content.style.width = panel.style.width;
	content.style.height = panel.style.height;
	content.style.top = panel.style.top;
	content.style.left = panel.style.left;
	content.style.visibility = "hidden";
	// 设置层内容
	var html = "";
	var sb = new StringBuffer();
	sb.append("<table align='center' border='0' height='100%' cellpadding='0' cellspacing='0'>");
	sb.append("<tr height='100%' align='middle'>");
	sb.append("<td><img src='" + this.img + "' border='0'>&nbsp;</td>");
	sb.append("<td class='" + this.textClassName + "' id=\"_loadLayerText\">" + this.text + "</td>"); // 存放提示文字的td
	sb.append("<td class='" + this.textClassName + "' id=\"_timeCountText\" nowrap></td>"); // 存放秒数的td
	sb.append("</tr>");
	sb.append("</table>");
	html = sb.toString();
	content.innerHTML = html;
	//
	return content;
};

/**
 * 隐藏层
 * @author lyz
 * 2009-03-18
 */
LoadLayer.prototype.show = function() {
	this.panel.style.visibility = "visible";
	this.content.style.visibility = "visible";
	//
	this.stopTimeCount();
	if (this.timeCount === true) {
		this.beginTimeCount();
	}
	return false;
};

/**
 * 显示层
 * @author lyz
 * 2009-03-18
 */
LoadLayer.prototype.hide = function() {
	this.content.style.visibility = "hidden";
	this.panel.style.visibility = "hidden";
	//
	this.stopTimeCount();
	return false;
};

/**
 * 开始显示耗时
 * @author lyz
 * 2010-02-01
 */
LoadLayer.prototype.beginTimeCount = function () {
	this.intervalId = window.setInterval(this.setTimeElaspsed, 1000);
};


/**
 * 设置耗时
 * @author lyz
 * 2010-02-01
 */
LoadLayer.prototype.setTimeElaspsed = function () {
	var pannel = document.getElementById("_timeCountText");
	if (pannel !== null) {
		var t = pannel.innerText;
		if (t === null || t.replace(/(^\s*)|(\s*$)/g, "") === "") {
			t = "1";
			pannel.innerText = t;
			return false;
		}
		//
		t = t.replace(/(^\s*)|(\s*$)/g, "");
		t = "" + (parseInt(t) + 1);
		while (t.length < 3) {
			t = t + " ";
		}
		pannel.innerText = t;
		return true;
	}
	return false;
};

/**
 * 取消显示耗时
 * @author lyz
 * 2010-02-01
 */
LoadLayer.prototype.stopTimeCount = function () {
	if (this.intervalId != -1) {
		window.clearInterval(this.intervalId);
		this.intervalId = -1;
	}
	//
	var pannel = document.getElementById("_timeCountText");
	if (pannel !== null) {
		pannel.innerText = "";
	}
};

/**
 * 获取加载层样式宽度
 * @author lyz
 * 2009-03-18
 */
LoadLayer.prototype.getStyleWidth = function(styleWidth, parentWidth) {
	// 当样式无定义
	if (styleWidth === "" || styleWidth == "undefined" || styleWidth == "auto") {
		styleWidth = "160px";
	}
	// 当样式定义为百分比
	else if (styleWidth.indexOf("%") != -1) {
		var factorWidth = parseInt(styleWidth.substring(0, styleWidth.indexOf("%"))) / 100;
		styleWidth = parseInt(parentWidth * factorWidth) + "px";
	}
	return styleWidth;
};

/**
 * 获取加载层样式高度
 * @author lyz
 * 2009-03-18
 */
LoadLayer.prototype.getStyleHeight = function(styleHeight, parentHeight) {
	// 当样式无定义
	if (styleHeight === "" || styleHeight == "undefined" || styleHeight == "auto") {
		styleHeight = "40px";
	}
	// 当样式定义为百分比
	else if (styleHeight.indexOf("%") != -1) {
		var factorHeight = parseInt(styleHeight.substring(0, styleHeight.indexOf("%"))) / 100;
		styleHeight = parseInt(parentHeight * factorHeight) + "px";
	}
	return styleHeight;
};

/********************辅助方法********************/

/**
 * 获取当前应用路径
 * @author lyz
 * 2009-03-16
 */
LoadLayer.prototype.getContextPath = function() {
	var url = location.href;
	var tmp = url.replace("http://", "").replace("https://", "");
	tmp = tmp.substring(tmp.indexOf("/") + 1, tmp.length);
	var context = tmp.substring(0, tmp.indexOf("/"));
	return "/" + context;
};

/**
 * 获取距离body的横坐标
 * @author lyz
 * 2009-03-18
 */
LoadLayer.prototype.getLeft = function(src) {
	var set = 0;
	if (src) {
		if (src.offsetParent) {
			set += src.offsetLeft + this.getLeft(src.offsetParent);
		}
		if (src.tagName.toUpperCase() != "BODY") {
			var x = parseInt(src.scrollLeft, 10);
			if (!isNaN(x)) {
				set -= x;
			}
		}
	}
	return set;
};

/**
 * 获取距离body的纵坐标
 * @author lyz
 * 2009-03-18
 */
LoadLayer.prototype.getTop = function(src) {
	var set = 0;
	if (src) {
		if (src.offsetParent) {
			set += src.offsetTop + this.getTop(src.offsetParent);
		}
		if (src.tagName.toUpperCase() != "BODY") {
			var y = parseInt(src.scrollTop, 10);
			if (!isNaN(y)) {
				set -= y;
			}
		}
	}
	return set;
};

/*****************其他工具类*****************/

function StringBuffer() {
    this._strings_ = new Array();
};

StringBuffer.prototype.append = function(str) {
    this._strings_.push(str);
};

StringBuffer.prototype.toString = function() {
    return this._strings_.join("");
};

/**
 * 样式转换工具类
 */
function StyleUtil() {
}

StyleUtil.prototype.getStyle = function(src, value) {
	var rs;
	if (src.style[value]) {
		rs = src.style[value];
	} else {
		if (window.getComputedStyle) {
			value = value.replace(/([A-Z])/g, "-$1");
			rs = window.getComputedStyle(src, "").getPropertyValue(value);
			if (value == "color") {
				rs = this.colorToHex(rs);
			}
		} else {
			if (src.currentStyle) {
				rs = src.currentStyle[value];
			} else {
				return "";
			}
		}
	}
	return rs;
};

StyleUtil.prototype.colorToHex = function(color) {
	var rgb = /rgb\((\d{1,3}),(\s)*(\d{1,3}),(\s)*(\d{1,3})\)/;
	var noun = /^\w+/;
	var rs;
	if (rgb.test(color)) {
		var rgbfix = rgb.exec(color);
		var r, g, b;
		r = parseInt(rgbfix[1]).toString(16);
		g = parseInt(rgbfix[3]).toString(16);
		b = parseInt(rgbfix[5]).toString(16);
		rs = "#" + (r < 10 ? "0" + r : r) + (g < 10 ? "0" + g : g) + (b < 10 ? "0" + b : b);
	} else {
		rs = color;
	}
	if (rs.length == 4 && !noun.test(rs)) {
		rs = rs + rs.split("#")[1];
	}
	return rs;
};