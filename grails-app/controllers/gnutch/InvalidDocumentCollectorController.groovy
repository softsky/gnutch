package gnutch

class InvalidDocumentCollectorController {

  def invalidDocumentCollectorService

  /**
     Render page using InvalidDocumentCollectorService#getDocuments() as model
  */
  def index() {
    return render(view: 'index', model: [documents: invalidDocumentCollectorService.documents])
  }

}
