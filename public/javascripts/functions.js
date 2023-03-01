/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


function StringToXMLDom(string) {
    var xmlDoc = null;
    if (window.DOMParser)
    {
        parser = new DOMParser();
        xmlDoc = parser.parseFromString(string, "text/xml");
    } else // Internet Explorer
    {
        xmlDoc = new ActiveXObject("Microsoft.XMLDOM");
        xmlDoc.async = "false";
        xmlDoc.loadXML(string);
    }
    return xmlDoc;
}

function xmlEscape(str) {
    var xmlEntity = [
        [/&/g, '&amp;'],
        [/</g, '&lt;'],
        [/>/g, '&gt;'],
        [/\\/g, '&apos;'],
        [/"/g, '&quot;'],
    ];
    for (var i = 0; i < xmlEntity.length; i++) {
        str = str.replace(xmlEntity[i][0], xmlEntity[i][1]);
    }
    return str;
}