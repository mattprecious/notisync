cTID = function(s) { return app.charIDToTypeID(s); };
sTID = function(s) { return app.stringIDToTypeID(s); };

function createLauncherIcon(size, file) {
    createIcon(size, file, 'Circle Dark', 'Textures Dark');
}

function createAbIcon(size, file) {
    createIcon(size, file, 'Circle Light', 'Textures Light');
}

function createIcon(size, file, circleName, texturesName) {
    var doc = app.activeDocument;
    var layers = doc.layers;
    
    var savedState = doc.activeHistoryState;
    
    for (var i = 0; i < layers.length; i++) {
        layers[i].visible = false;
    }
    
    var circle = layers.getByName(circleName);
    var bang = layers.getByName('Bang');
    var textures = layers.getByName(texturesName);
    
    circle.visible = true;
    bang.visible = true;
    textures.visible = true;
    
    //var width = circle.bounds[2] - circle.bounds[0];
    //var height = circle.bounds[3] - circle.bounds[1];
    var width = doc.width;
    var height = doc.height;
    var scaleX = size / width * 100;
    var scaleY = size / height * 100;
    var factor = Math.min(scaleX, scaleY);
    
    circle.resize(factor, factor, AnchorPosition.MIDDLECENTER);
    bang.resize(factor, factor, AnchorPosition.MIDDLECENTER);
    
    // the script output is 1px off from the action output, no idea why
    bang.translate(0, -1);
    
    // had to copy this from ActionToJavascript output, no idea how to simplify these...
    // would like to rewrite without action objects if possible
    
    // select textures dark layer
    var desc1 = new ActionDescriptor();
    var ref1 = new ActionReference();
    ref1.putName(cTID('Lyr '), textures.name);
    desc1.putReference(cTID('null'), ref1);
    desc1.putBoolean(cTID('MkVs'), false);
    executeAction(cTID('slct'), desc1, DialogModes.NO);
    
    // remove layer mask
    var desc1 = new ActionDescriptor();
    var ref1 = new ActionReference();
    ref1.putEnumerated(cTID('Chnl'), cTID('Ordn'), cTID('Trgt'));
    desc1.putReference(cTID('null'), ref1);
    executeAction(cTID('Dlt '), desc1, DialogModes.NO);
    
    // set selection to circle dark
    var desc1 = new ActionDescriptor();
    var ref1 = new ActionReference();
    ref1.putProperty(cTID('Chnl'), sTID("selection"));
    desc1.putReference(cTID('null'), ref1);
    var ref2 = new ActionReference();
    ref2.putEnumerated(cTID('Chnl'), cTID('Chnl'), cTID('Trsp'));
    ref2.putName(cTID('Lyr '), circle.name);
    desc1.putReference(cTID('T   '), ref2);
    executeAction(cTID('setd'), desc1, DialogModes.NO);
    
    // subtract bang selection
    var desc1 = new ActionDescriptor();
    var ref1 = new ActionReference();
    ref1.putEnumerated(cTID('Chnl'), cTID('Chnl'), cTID('Trsp'));
    ref1.putName(cTID('Lyr '), bang.name);
    desc1.putReference(cTID('null'), ref1);
    var ref2 = new ActionReference();
    ref2.putProperty(cTID('Chnl'), sTID("selection"));
    desc1.putReference(cTID('From'), ref2);
    executeAction(cTID('Sbtr'), desc1, DialogModes.NO);
    
    // add layer mask to textures
    var desc1 = new ActionDescriptor();
    desc1.putClass(cTID('Nw  '), cTID('Chnl'));
    var ref1 = new ActionReference();
    ref1.putEnumerated(cTID('Chnl'), cTID('Chnl'), cTID('Msk '));
    desc1.putReference(cTID('At  '), ref1);
    desc1.putEnumerated(cTID('Usng'), cTID('UsrM'), cTID('RvlS'));
    executeAction(cTID('Mk  '), desc1, DialogModes.NO);

    // end Action stuff
    
    // resize canvas
    doc.resizeCanvas(size, size);
    
    // save to png
    var opts = new ExportOptionsSaveForWeb();
    opts.format = SaveDocumentType.PNG;
    opts.PNG8 = false;
    opts.quality = 100;
    activeDocument.exportDocument(file, ExportType.SAVEFORWEB, opts);
    
    // restore history
    doc.activeHistoryState = savedState;
}

function createNotificationIcon(size, file, version) {
        var doc = app.activeDocument;
    var layers = doc.layers;
    
    var savedState = doc.activeHistoryState;
    
    for (var i = 0; i < layers.length; i++) {
        layers[i].visible = false;
    }
    
    var layer = layers.getByName('Notification v' + version);
    layer.visible = true;
    
    // resize image
    doc.resizeImage(size, size);
    
    // save to png
    var opts = new ExportOptionsSaveForWeb();
    opts.format = SaveDocumentType.PNG;
    opts.PNG8 = false;
    opts.quality = 100;
    activeDocument.exportDocument(file, ExportType.SAVEFORWEB, opts);
    
    // restore history
    doc.activeHistoryState = savedState;
}

var resPath = 'D:/Android/notisync/Notisync/res';
var densities = [
    'ldpi',
    'mdpi',
    'hdpi',
    'xhdpi',
    'xxhdpi',
];

var launcherSizes = {
    'ldpi': 36,
    'mdpi': 48,
    'hdpi': 72,
    'xhdpi': 96,
    'xxhdpi': 144,
};

var notificationSizes = {
    'ldpi': 18,
    'mdpi': 24,
    'hdpi': 36,
    'xhdpi': 48,
    'xxhdpi': 72,
}

var notificationSizesV9 = {
    'ldpi': 12,
    'mdpi': 18,
    'hdpi': 24,
    'xhdpi': 36,
    'xxhdpi': 48,
}

for (var i in densities) {
    var density = densities[i];

    createLauncherIcon(launcherSizes[density], new File(resPath + '/drawable-' + density + '/ic_launcher.png'));
    createAbIcon(launcherSizes[density], new File(resPath + '/drawable-' + density + '/ab_home.png'));
    createNotificationIcon(notificationSizesV9[density], new File(resPath + '/drawable-' + density + '-v9/ic_stat_logo.png'), 9);
    createNotificationIcon(notificationSizes[density], new File(resPath + '/drawable-' + density + '-v11/ic_stat_logo.png'), 11);
}