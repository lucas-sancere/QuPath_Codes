

// **IMPORT_XML**

// Import and convert Aperio ImageScope XML annotations into QuPath by parsing vertex coordinates, reconstructing polygon ROIs, and adding them to the project hierarchy.

// To use this script you must have .xml files with exact same path as the images, but with .xml extension instead
// Can run for all images within a project 


import qupath.lib.scripting.QP
import qupath.lib.geom.Point2
import qupath.lib.roi.PolygonROI
import qupath.lib.objects.PathAnnotationObject
import groovy.xml.XmlSlurper

def rotated = false
def project = QP.getProject()
if (project == null) {
    print "No project found!"
    return
}

project.getImageList().each { entry ->

    def imageData = entry.readImageData()
    def server = imageData.getServer()
    def h = server.getHeight()
    def hierarchy = imageData.getHierarchy()

    def uri = entry.getServerBuilder().getURIs().get(0)
    def imageFile = new File(uri)
    def xmlFileName = imageFile.getName().replaceFirst(/\.[^.]+$/, '.xml')
    def xmlFile = new File(imageFile.getParentFile(), xmlFileName)

    if (!xmlFile.exists()) {
        print "⚠️ No XML found for image: ${imageFile.name} (expected ${xmlFile.path}) — skipping."
        return // ← returns from THIS iteration, NOT entire script
    }

    print "📥 Importing XML for: ${imageFile.name}"

    def text = xmlFile.getText('UTF-8')
    def list = new XmlSlurper().parseText(text)

    list.Annotation.each { ann ->
        ann.Regions.Region.each { region ->
            def pts = []

            region.Vertices.Vertex.each { vertex ->
                double X, Y

                if (rotated) {
                    X = vertex.@Y.toDouble()
                    Y = h - vertex.@X.toDouble()
                } else {
                    X = vertex.@X.toDouble()
                    Y = vertex.@Y.toDouble()
                }

                pts.add(new Point2(X, Y))
            }

            if (pts.size() >= 3) {
                def annotation = new PathAnnotationObject(new PolygonROI(pts))
                hierarchy.addObject(annotation, false)
            }
        }
    }

    entry.saveImageData(imageData)
}

project.syncChanges()
print "✅ Finished importing available XML files."
