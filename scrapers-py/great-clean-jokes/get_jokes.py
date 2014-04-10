#!/usr/bin/python
"""
    This class is used for getting jokes categories
"""
import json
import os
from sets import Set

import urllib2
from lxml import etree

# user imports
import sqlalchemy
from sqlalchemy.sql.elements import and_
from db.database import Storage, meta

import constants
from html_text_extractor import HtmlTextExtractor


class JokesScraper:
    def __init__(self):
        self.storage = Storage()
        self.jokes_set = Set()

    def get_jokes(self):
        categories = self.get_categories()
        jokes = []
        for category in categories:
            jokes_for_category = self.get_jokes_by_category(category)
            self.process_jokes(jokes_for_category, category)
            jokes.extend(jokes_for_category)
            print

        return jokes

    def get_jokes_by_category(self, category):
        print "Getting jokes for category {0}".format(category['name'])
        all_jokes = []
        [jokes_on_page, older_posts] = self.get_jokes_on_page(category["url"])
        all_jokes.extend(jokes_on_page)
        while older_posts:
            url = older_posts[0].attrib["href"]
            print "Getting next page " + url
            [jokes_on_page, older_posts] = self.get_jokes_on_page(url)
            all_jokes.extend(jokes_on_page)
        return all_jokes

    def get_jokes_on_page(self, url):
        req = urllib2.Request(url=url,
                              headers={'User-Agent': constants.USER_AGENT})
        response = urllib2.urlopen(req)
        html_parser = etree.HTMLParser()
        tree = etree.parse(response, html_parser)
        jokes = tree.xpath(constants.JOKES_SELECTOR)
        older_posts = tree.xpath(constants.OLDER_POSTS_SELECTOR)
        for element in jokes:
            print element.text + " " + element.attrib["href"]
        return [jokes, older_posts]

    def get_categories(self):
        self.storage.connect()
        categories =  meta.tables['subcategories']
        query = sqlalchemy.select([categories.c.id,
                                    categories.c.name,
                                    categories.c.url])
        results = self.storage.execute(query)
        return results

    def process_jokes(self, jokes, category):
        for joke in jokes:
            joke_data = self.process_joke(joke.attrib['href'])
            if joke_data:
                self.save_joke_to_db(joke_data, category)

    def process_joke(self, url):
        req = urllib2.Request(url=url,
                              headers={'User-Agent': constants.USER_AGENT})
        response = urllib2.urlopen(req)
      #  print response.read()

        html_parser = etree.HTMLParser()
        tree = etree.parse(response, html_parser)

        title = tree.xpath(constants.TITLE_SELECTOR)
        content = tree.xpath(constants.CONTENT_SELECTOR)
        text_extractor = HtmlTextExtractor(content)
        content_text = text_extractor.extract_text()
        rating = tree.xpath(constants.RATING_SELECTOR)
        votes = tree.xpath(constants.VOTES_SELECTOR)
        comments_count = tree.xpath(constants.COMMENTS_SELECTOR)
        likes = 0 # self.fb_social_plugin_get_likes(tree, url)
        if title and content_text and rating and votes:
            print title[0].text
            print content_text
            print rating[0].text
            print votes[0].text
            if comments_count:
                print comments_count[0].text
                count = comments_count[0].text.split(' ')[0]
                if count == 'No':
                    count = 0
            else:
                count = 0
            print count

            return {"title" : title[0].text,
                    "content" : content_text,
                    "rating" : rating[0].text,
                    "votes" : votes[0].text,
                    "likes": likes,
                    'url' : url,
                    "comments_count" : count}

        return None

        #self.get_comments(url)

    def fb_social_plugin_get_likes(self, tree, joke_url):
        # TODO
        likes = tree.xpath("/html//div[@data-href='" + joke_url + "']")
        print likes
        if likes:
            print likes[0].attrib
            print "Requesting to facebook " +  likes[0].attrib['fb-iframe-plugin-query']
            url =  likes[0].attrib['fb-iframe-plugin-query']
            req = urllib2.Request(url=url,
                            headers={'User-Agent': constants.USER_AGENT})

            response = urllib2.urlopen(req)
            html_parser = etree.HTMLParser()
            tree = etree.parse(response, html_parser)
            likes_count = tree.xpath("/html//span[@class='pluginCountTextDisconnected']")
            if likes_count:
                print "Likes " + likes_count[0].text
                count = likes_count[0].text
            else:
                count = 0
            print count
            return count

    def save_joke_to_db(self, joke_data, category):
        joke_id = self.get_joke_id(joke_data['title'])
        if joke_id is None:
            print "New Joke. Inserting into db"
            self.insert_joke_in_db(joke_data)
        elif joke_data['title'] not in self.jokes_set:
            print "New scanning of joke"
            self.update_joke_in_db(joke_id, joke_data)
            self.jokes_set.add(joke_data['title'])

        if not self.joke_in_category(joke_id, category):
            print "Joke not present in category " + category['name']
            self.add_joke_to_category(joke_data, category)

    def get_joke_id(self, joke_title):
        print "Getting joke id"
        self.storage.connect()
        jokes = meta.tables['joke']
        query = sqlalchemy.select([jokes.c.id]).where(jokes.c.title == joke_title)
        results = self.storage.execute(query)
        self.storage.disconnect()
        for result in results:
            print result["id"]
            return result["id"]
        return None

    def insert_joke_in_db(self, data):
        self.storage.insert('joke', data)

    def update_joke_in_db(self, joke_id, joke_data):
        self.storage.connect()
        jokes = meta.tables['joke']
        query = sqlalchemy.update(jokes).where(jokes.c.id==joke_id).values(joke_data)
        print query
        self.storage.execute(query)
        self.storage.disconnect()

    def joke_in_category(self, joke_id, category):
        self.storage.connect()
        jokes_categories = meta.tables['joke_category']
        query = sqlalchemy.select([jokes_categories.c.id]).where(
              and_(
                jokes_categories.c.joke_id == joke_id,
                jokes_categories.c.subcategory_id == category['id']
            )
        )
        results = self.storage.execute(query)
        self.storage.disconnect()
        for result in results:
            print result["id"]
            return True
        return False

    def add_joke_to_category(self, joke_data, category):
        joke_id = self.get_joke_id(joke_data['title'])
        insert_data = {"joke_id": joke_id,
                       "subcategory_id": category['id']
                       }
        self.storage.insert('jokes_categories', insert_data)

    def get_comments(self, url):
        # TODO find out how comments are loaded

        req = urllib2.Request(url="{0}/#discuss_thread".format(url),
                              headers={'User-Agent': constants.USER_AGENT})
        response = urllib2.urlopen(req)
        print response.read()

        # html_parser = etree.HTMLParser()
        # tree = etree.parse(response, html_parser)
        #
        # posts = tree.xpath(constants.COMMENTS_SELECTOR)
        # print posts
        # for post in posts:
        #     author = post.xpath("//span[@class='author]")[0]
        #     timestamp = post.xpath("//a[@class='time-ago']")[0]
        #     content = post.xpath("//div[@class='post-message']/p[1]")[0]
        #
        #     print author.text
        #     print timestamp.text
        #     print content.text

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
        categories = meta.tables['category']
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
        categories_table = meta.tables['category']
        insert_data = {"name": category['name'],
                       "url": category['url']
                       }

        query = sqlalchemy.insert(categories_table, insert_data)
        print query
        storage.execute(query)
        storage.disconnect()

        category_id = self.get_category_id(storage, category['name'])

        storage.connect()
        subcategories_table = meta.tables['subcategory']
        for subcategory in category['subcategories']:
            query = sqlalchemy.insert(subcategories_table,
                                      {"name" : subcategory['name'], "url" : subcategory['url'],
                                       "category_id": category_id})
            print query
            storage.execute(query)

        storage.disconnect()

    def print_unicode(self, string):
        try:
            print string
        except UnicodeEncodeError as e:
            print u"Unicode Error: {0}".format(e.encoding)

if __name__ == "__main__":
    scraper = JokesScraper()
    scraper.get_jokes()
    # scraper.save_to_db()


