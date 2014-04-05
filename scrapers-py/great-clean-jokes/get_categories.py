#!/usr/bin/python
"""
    This class is used for getting jokes categories
"""
import json
import os

import urllib2
from lxml import etree

# user imports
import sqlalchemy
from db.database import Storage, meta

import constants


class CategoriesScraper:

    URL_ROOT = constants.ROOT_URL

    def __init__(self):
        self.categories = []

    def get_categories(self):
        url = self.URL_ROOT
        req = urllib2.Request(url=url,
                              headers={'User-Agent': constants.USER_AGENT})
        response = urllib2.urlopen(req)
        html_parser = etree.HTMLParser()
        tree = etree.parse(response, html_parser)
        categories_selector = "/html//ul[@class='FoldingCategoryList nodeLevel0']/li/a[1]"

        categories = tree.xpath(categories_selector)
        for element in categories:
            category = dict()
            category['name'] = element.text
            category['url'] = element.attrib['href']
            category['subcategories'] = []

            subcategs = element.xpath("../ul[@class='nodeLevel1']/li/a[1]")
            for subcateg in subcategs:
                subcategory = {"name" : subcateg.text,
                              "url" : subcateg.attrib['href']
                              }
                category['subcategories'].append(subcategory)

            self.categories.append(category)
        print self.categories
        return self.categories


    def save_json(self, json_resp):
        self.create_directory()
        print "Saving json to disk..."
        with open(u"{0}/json/response.json".format(self.path), "w") as f:
            json.dump(json_resp, f)

    def parse(self, json_resp):
        authors = json_resp['d']['Author']['Result']
        return authors

    def get_category_id(self, storage, name):
        storage.connect()
        categories = meta.tables['categories']
        query = sqlalchemy.select([categories.c.id]).where(categories.c.name == name)
        results = storage.execute(query)
        storage.disconnect()
        for result in results:
            print result["id"]
            return result["id"]

    def save_to_db(self):
        storage = Storage()
        print "Saving categories to db..."
        map(lambda category: self.save_category_to_db(category, storage), self.categories)

    def save_category_to_db(self, category, storage):
        storage.connect()
        categories_table = meta.tables['categories']
        insert_data = {"name": category['name'],
                       "url": category['url']
                       }

        query = sqlalchemy.insert(categories_table, insert_data)
        print query
        storage.execute(query)
        storage.disconnect()

        category_id = self.get_category_id(storage, category['name'])

        storage.connect()
        subcategories_table = meta.tables['subcategories']
        for subcategory in category['subcategories']:
            query = sqlalchemy.insert(subcategories_table,
                                      {"name" : subcategory['name'], "url" : subcategory['url'],
                                       "category_id": category_id})
            print query
            storage.execute(query)

        storage.disconnect()


    def create_directory(self):
        print "Creating directory for {0}".format(self.domain_id)
        dirs = u"{0}/json".format(self.path)
        self.print_unicode(dirs)
        if not os.path.exists(dirs):
            os.makedirs(dirs)

    def print_unicode(self, string):
        try:
            print string
        except UnicodeEncodeError as e:
            print u"Unicode Error: {0}".format(e.encoding)

if __name__ == "__main__":
    scraper = CategoriesScraper()
    categories = scraper.get_categories()
    scraper.save_to_db()


