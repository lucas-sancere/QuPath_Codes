

// **EXPORT_MASKS**

// The following script export a mask from a choosen annotation. The mask can be downsampled. 


// One must set the output path here "def pathOutput = buildFilePath" 

import qupath.lib.images.servers.LabeledImageServer

def imageData = getCurrentImageData()

// Define output path (relative to project)
def name = GeneralTools.getNameWithoutExtension(imageData.getServer().getMetadata().getName())
def pathOutput = buildFilePath('/home/lsancere/These/CMMC/Local_DATA/SCC/ProcessedData/ExportMasks/', name)
mkdirs(pathOutput)

// Define output resolution

// double requestedPixelSize = 1.0

// Convert to downsample

// double downsample = requestedPixelSize / imageData.getServer().getPixelCalibration().getAveragedPixelSize()
double downsample = 32.0 //To follow Whole-SlideIPP Pipeline


def labelServer = new LabeledImageServer.Builder(imageData)
  .backgroundLabel(0, ColorTools.BLACK) // Specify background label (usually 0 or 255)
  .downsample(downsample)    // Choose server resolution; this should match the resolution at which tiles are exported
  .useUniqueLabels()         // Assign labels based on instances, not classifications
  .multichannelOutput(false) // If true, each label refers to the channel of a multichannel binary image (required for multiclass probability)
  .useFilter({p -> p.getPathClass() == getPathClass('Stroma')}) // Accept only objects classified as 'Gland'
  .build()


// Export each 

def outputPath = buildFilePath(pathOutput, 'StromaPatch_' + name + '.png')
    writeImage(labelServer, outputPath)



print 'Done!'