

// **EXPORT_INDIV_ANNOTATIONS**

// The following script creates a LabeledImageServer, and loops through all annotations in an image with the 
// choosen clasifications – exporting a labeled image for the bounding box of each annotation
// Coming from QuPath documentation

// One must set the output path here "def pathOutput = buildFilePath" and add the corresponding labels names on the "add.label" lines

import qupath.lib.images.servers.LabeledImageServer

def imageData = getCurrentImageData()

// Define output path (relative to project)
def name = GeneralTools.getNameWithoutExtension(imageData.getServer().getMetadata().getName())
def pathOutput = buildFilePath('/home/lsancere/These/CMMC/Local_DATA/SCC/ProcessedData/ExportSingleAnnotations/', name)
mkdirs(pathOutput)

// Define output resolution

// double requestedPixelSize = 1.0

// Convert to downsample

// double downsample = requestedPixelSize / imageData.getServer().getPixelCalibration().getAveragedPixelSize()
double downsample = 32.0 //To follow Whole-SlideIPP Pipeline


// Create an ImageServer where the pixels are derived from annotations
def labelServer = new LabeledImageServer.Builder(imageData)
    .backgroundLabel(0, ColorTools.BLACK) // Specify background label (usually 0 or 255)
    .downsample(downsample)    // Choose server resolution; this should match the resolution at which tiles are exported
    .addLabel('Tumor', 255, ColorTools.WHITE )      // Choose output labels (the order matters!)
    // .addLabel('Stroma', 2)
    // .addLabel('Connective', 3)
    // .addLabel('Dead', 4)
    // .addLabel('Non-Neoplastic Epithelial', 5)    
    .lineThickness(2)          // Optionally export annotation boundaries with another label
    .setBoundaryLabel('Boundary*', 255, ColorTools.WHITE) // Define annotation boundary label
    .multichannelOutput(false) // If true, each label refers to the channel of a multichannel binary image (required for multiclass probability)
    .build()


// Export each region
int i = 0
for (annotation in getAnnotationObjects()) {
    def region = RegionRequest.createInstance(
        labelServer.getPath(), downsample, annotation.getROI())
    i++
    def outputPath = buildFilePath(pathOutput, 'Region ' + i + '.png')
    writeImageRegion(labelServer, region, outputPath)
}


print 'Done!'