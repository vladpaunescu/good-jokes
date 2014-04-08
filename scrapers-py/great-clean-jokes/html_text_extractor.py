import urllib2
from lxml import etree

import constants


class HtmlTextExtractor:

    def __init__(self, nodes):
        self.nodes = nodes

    def extract_text(self):
        text = []
        for node in self.nodes:
            for text_node in node.itertext():
                text.append(text_node)
            text.append("\n")

        print "".join(text)
        return "".join(text)

if __name__ == "__main__":
      req = urllib2.Request(url='http://en.wikipedia.org/wiki/Space',
                            headers={'User-Agent': constants.USER_AGENT})

      response = urllib2.urlopen(req)
      html_parser = etree.HTMLParser()
      tree = etree.parse(response, html_parser)
      content = tree.xpath("/html//div[@id='mw-content-text']/p")

      text_extractor = HtmlTextExtractor(content)
      text_extractor.extract_text()

      req = urllib2.Request(url='http://www.greatcleanjokes.com/3501/prison-joke/',
                            headers={'User-Agent': constants.USER_AGENT})

      response = urllib2.urlopen(req)
      html_parser = etree.HTMLParser()
      tree = etree.parse(response, html_parser)
      content = tree.xpath(constants.CONTENT_SELECTOR)


      text_extractor = HtmlTextExtractor(content)
      text_extractor.extract_text()

      req = urllib2.Request(url='http://www.greatcleanjokes.com/418/418/',
                            headers={'User-Agent': constants.USER_AGENT})
      response = urllib2.urlopen(req)
      html_parser = etree.HTMLParser()
      tree = etree.parse(response, html_parser)
      content = tree.xpath(constants.CONTENT_SELECTOR)


      text_extractor = HtmlTextExtractor(content)
      text_extractor.extract_text()

