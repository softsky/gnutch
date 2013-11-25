package gnutch.exception

class ExceptionCollectorServiceController {

  def exceptionCollectorService

  def index() { 
    def exceptions = exceptionCollectorService.exceptions
    render(view: 'index', model: [exceptions: exceptions])
  }
}
