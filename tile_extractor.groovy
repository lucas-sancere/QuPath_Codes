import qupath.lib.images.servers.LabeledImageServer

def imageData = getCurrentImageData()

// Define output path (relative to project)
def name = GeneralTools.getNameWithoutExtension(imageData.getServer().getMetadata().getName())
def pathOutput = buildFilePath("/home/lsancere/These/CMMC/Local_DATA/SCC/ProcessedData/LabelExctracted", name)
mkdirs(pathOutput)


// DOWNSAMPLE
// a) You can calculate the downsample factor based on requestedPixelSize

// Define output resolution in µm
// double requestedPixelSize = 5.0
// double downsample = requestedPixelSize / imageData.getServer().getPixelCalibration().getAveragedPixelSize()

// b) or you can specify it yourself (1.0 means original magnification)
double downsample = 1.0 // original mag = 40 ; downsampled mag = 40/8 = 5

// Create an ImageServer where the pixels are derived from annotations
def labelServer = new LabeledImageServer.Builder(imageData)
    .backgroundLabel(0, ColorTools.WHITE) // Specify background label (usually 0 or 255)
//     .downsample(downsample)    // Choose server resolution; this should match the resolution at which tiles are exported
    .addLabel('Connective', 1)      // Choose output labels (the order matters!)
    .addLabel('Neoplastic', 2)     // "Stroma" here means non-tumor
    .addLabel('Dead',3)
    .addLabel('Inflammatory',4)
    .multichannelOutput(false)  // If true, each label is a different channel (required for multiclass probability)
    .build()

// Create an exporter that requests corresponding tiles from the original & labeled image servers
new TileExporter(imageData)
//    .downsample(downsample)     // Define export resolution
    .imageExtension('.jpg')     // Define file extension for original pixels (often .tif, .jpg, '.png' or '.ome.tif')
    .tileSize(10000)              // Define size of each tile, in pixels
    .labeledServer(labelServer) // Define the labeled image server to use (i.e. the one we just built)
    .annotatedTilesOnly(true)  // If true, only export tiles if there is a (labeled) annotation present
    .overlap(0)                // Define overlap, in pixel units at the export resolution
    .writeTiles(pathOutput)     // Write tiles to the specified directory

print 'Done!'