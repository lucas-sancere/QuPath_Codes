

// EXPORT_SELECTED_OBJECT


// Write the region of the image corresponding to the currently-selected object


def roi = getSelectedROI()
def requestROI = RegionRequest.createInstance(server.getPath(), 1, roi)
writeImageRegion(server, requestROI, '/home/lsancere/These/CMMC/Local_DATA/SCC/ProcessedData/ExportSelected/region.tif')