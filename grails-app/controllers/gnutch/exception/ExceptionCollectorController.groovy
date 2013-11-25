package gnutch.exception

class ExceptionCollectorController {

  def exceptionCollectorService

  def index() { 
    def exceptions = exceptionCollectorService.exceptions
    render(view: 'index', model: [exceptions: exceptions])
  }
}
