from flask import Flask
from flask import request
from flask import Response
import xml.etree.ElementTree as ET
import re

app = Flask(__name__)
@app.route("/ws/scbCashPaymentsInitiation.v1.ws.provider.Initiation/scbCashPaymentsInitiation_v1_ws_provider_Initiation_Port", methods=['POST'])                     
def stub():
  response=read_response('sample-verify-payment-response.xml')
  data=request.stream.read()
  print data
  resp = Response(response, status=200, mimetype='text/xml')
  return resp

def read_response(file_name):
  response=''
  with open(file_name) as f:
      for line in f:
        response = response + line.strip() + '\n'
  return response
    
if __name__ == '__main__':
    app.run(debug=True,host='0.0.0.0',port=50002)
