#!/usr/bin/python
ROOT_URL = "http://www.greatcleanjokes.com/"
USER_AGENT = "Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 " \
             "(KHTML, like Gecko) Chrome/33.0.1750.154 Safari/537.36"

# selectors for jokes

JOKES_SELECTOR = "/html//h2[@class='entry-title post-title']/a"
OLDER_POSTS_SELECTOR = "/html//div[@class='navigation']//div[@class='previous']/a"

TITLE_SELECTOR = "/html//h1[@class='entry-title post-title']"
CONTENT_SELECTOR = "/html//div[@class='post-entry']/p[1]"
RATING_SELECTOR = "/html//span[@class='rating']/span[@class='average']"
VOTES_SELECTOR = "/html//span[@class='rating']/span[@class='votes']"
COMMENTS_SELECTOR = "/html//span[@class='comments-link']//a[1]/span[1]"



