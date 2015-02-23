#coding=utf-8
__author__ = 'Sun'

import zmq


if __name__ == "__main__":
    context = zmq.Context()

    #  Socket to talk to server
    print("Connecting to SRL server ")
    socket = context.socket(zmq.REQ)
    socket.connect("tcp://localhost:8090")


    raw_sentences = b'''向影心出生在陕西西安城郊一个有名的郎中家庭。
        她天生丽质，聪颖好学，琴棋书画无所不通。
        年方十八，成为方圆百里有名的才女。
        尽管登门求婚者络绎不绝，可一个又一个门当户对的小伙子都遭到了向影心的拒绝。
        这可使视她为掌上明珠的父母伤透了脑筋，猜不透女儿究竟要选一个什么样的如意郎君。'''


    socket.send(raw_sentences)

    #  Get the reply.
    message = socket.recv()
    print(message)


