# user imports
import os
import sqlalchemy
from db.database import Storage, meta
from sqlalchemy.sql import and_, or_, not_
import json
import dicttoxml

class JokesXmlCreator:
    def __init__(self, out_dir):
        self.out_dir = out_dir
        self.storage = Storage()

    def save_jokes(self):
        jokes = self.load_jokes_from_db()

        for idx, joke, in enumerate(jokes):
            self.save_joke(idx, joke)


    def load_jokes_from_db(self):
        self.storage.connect()
        jokes = meta.tables['joke']
        query = sqlalchemy.select([jokes.c.id,
                                   jokes.c.title,
                                   jokes.c.content,
                                   jokes.c.rating,
                                   jokes.c.votes,
                                   jokes.c.url,
                                   jokes.c.comments_count])
        results = self.storage.execute(query)
        self.storage.disconnect()

        return results

    def save_joke(self, idx, joke):
        print "Saving joke " + joke["title"]
        self.storage.connect()
        joke_data = dict(joke.items())
        joke_data['categories'] = []

        jokes_categories = meta.tables['joke_category']
        subcategories = meta.tables['subcategory']
        categories = meta.tables['category']
        query = sqlalchemy.select([subcategories.c.id.label("subcategory_id"),
                                   categories.c.id.label("category_id"),
                                   subcategories.c.name.label("subcategory_name"),
                                   categories.c.name.label("category_name")]).where(
            and_(
                joke["id"] == jokes_categories.c.joke_id,
                jokes_categories.c.subcategory_id == subcategories.c.id,
                subcategories.c.category_id == categories.c.id)
        )

        print query
        results = self.storage.execute(query)
        self.storage.disconnect()

        for result in results:
            joke_data['categories'].append({'category_id': str(result['category_id']),
                                    'subcategory_id': str(result['subcategory_id']),
                                    'category_name': result['category_name'],
                                    'subcategory_name': result['subcategory_name']})
        print joke_data

        self.save_joke_on_disk(idx, joke_data)

    def save_joke_on_disk(self, idx, joke):
        if not os.path.exists(self.out_dir):
            os.makedirs(self.out_dir)
        with open("{0}/joke-{1}.json".format(self.out_dir, idx), "w") as f:
            data = json.dumps(joke, encoding='utf-8', ensure_ascii=False)
            f.write(data.encode("utf-8"))
        with open("{0}/joke-{1}.xml".format(self.out_dir, idx), "w") as f:
            dicttoxml.set_debug()
            joke['votes'] = str(joke['votes'])
            joke['id'] = str(joke['id'])
            joke['comments_count'] = str(joke['comments_count'])
            xml = dicttoxml.dicttoxml(joke)
            f.write(xml.encode('utf-8'))



if __name__ == "__main__":
    xml_creator = JokesXmlCreator("out")
    xml_creator.save_jokes()





