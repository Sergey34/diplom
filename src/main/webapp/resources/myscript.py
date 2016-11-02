import sys

name = sys.argv[1]
f = open('/home/sergey/workspace/IdeaProjects/diplom/diplom/src/main/resources/' + name + '.txt', 'w')
f.write("Hello")
