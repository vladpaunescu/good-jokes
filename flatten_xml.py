from lxml import etree


def extract_text(xmlfile):
    tree = etree.parse(xmlfile)
    turns = tree.xpath('//Turn')
    dialog = []
    for turn in turns:
        author = turn.attrib['nickname']
        line = ""
        for txt in turn.itertext():
            if not txt.isspace():
                line += txt

        dialog.append({'author': author, 'message' : line})
    txt = ""
    for line in dialog:
        txt += "{0}: {1}\n".format(line['author'], line['message'])

    return txt


if __name__ == "__main__":
    text = extract_text('5.xml')
    with open("log.txt", "w") as f:
        f.write(text)





