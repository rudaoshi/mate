#coding=utf-8
__author__ = 'Sun'

import socket
import sys

# Create a TCP/IP socket
sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)

# Connect the socket to the port where the server is listening
server_address = ('localhost', 8090)
print >>sys.stderr, 'connecting to %s port %s' % server_address
sock.connect(server_address)

try:

    # Send data
    message = '''向影心出生在陕西西安城郊一个有名的郎中家庭。
    她天生丽质，聪颖好学，琴棋书画无所不通。
    年方十八，成为方圆百里有名的才女。
    尽管登门求婚者络绎不绝，可一个又一个门当户对的小伙子都遭到了向影心的拒绝。
    这可使视她为掌上明珠的父母伤透了脑筋，猜不透女儿究竟要选一个什么样的如意郎君。'''

    print >>sys.stderr, 'sending "%s"' % message
    sock.sendall(message)

    # Look for the response
    amount_received = 0
    amount_expected = len(message)

    while amount_received < amount_expected:
        data = sock.recv(16)
        amount_received += len(data)
        print >>sys.stderr, 'received "%s"' % data

finally:
    print >>sys.stderr, 'closing socket'
    sock.close()