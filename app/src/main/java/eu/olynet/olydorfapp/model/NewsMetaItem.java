package eu.olynet.olydorfapp.model;

import java.util.Date;

/**
 * @author Martin Herrmann <a href="mailto:martin.herrmann@olynet.eu">martin.herrmann@olynet.eu<a>
 */
public class NewsMetaItem extends AbstractMetaItem<NewsMetaItem> {

    protected String title;
    protected String author = null;
    protected Organization organization;

    public NewsMetaItem() {
        super();
    }

    public NewsMetaItem(Date lastUsed) {
        super(lastUsed);
        this.title = null;
        this.author = null;
        this.organization = null;
    }

    public NewsMetaItem(long id) {
        super(id);
        this.title = null;
        this.author = null;
        this.organization = null;
    }

    public NewsMetaItem(long id, Date date, Date lastUpdated, String title, String author,
                        Organization organization) {
        super(id, date, lastUpdated);
        this.title = title;
        this.author = author;
        this.organization = organization;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;
        if (!(obj instanceof NewsMetaItem))
            return false;
        NewsMetaItem item = (NewsMetaItem) obj;

        return this.getId() == item.getId();
    }

    @Override
    public void updateItem(NewsMetaItem updatedItem) throws ItemMismatchException{
        super.updateItem(updatedItem);
        this.title = updatedItem.title;
        this.author = updatedItem.author;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public Organization getOrganization() {
        return organization;
    }

    @Override
    public String toString() {
        String result = super.toString() + "\n";
        result += "title = " + this.title + "\n";
        result += "author = " + this.author + "\n";
        result += "organization = " + this.organization;

        return result;
    }
}
