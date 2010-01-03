from org.rs377d.util import DefaultLogger

class PyLogger(object):
	def write(self, message):
		DefaultLogger.OUT.write(message)