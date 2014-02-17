package gnutch.exception

import java.util.Collections

class ExceptionCollectorController {

  def exceptionCollectorService

  def index() { 
    def exceptions
    synchronized(exceptionCollectorService.exceptions){
      exceptions = exceptionCollectorService.exceptions.clone()
    }
    render(view: 'index', model: [exceptions: exceptions])
  }
}
