package com.easyclipin.demo1.model;

import javafx.beans.Observable;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.util.Callback;
import java.util.Objects;

public class Link
{
    private final StringProperty name = new SimpleStringProperty(this, "Name", "");
    private final StringProperty link = new SimpleStringProperty(this, "Link", "");

    public Link() {}

    public Link(String name, String link)
    {
        this.name.set(name);
        this.link.set(link);
    }
    public void setName(String name)
    {
        this.name.set(name);
    }

    public String getName()
    {
        return name.get();
    }

    public StringProperty nameProperty() {
        return name;
    }

    public void setLink(String link)
    {
        this.link.set(link);
    }

    public String getLink()
    {
        return link.get();
    }

    public StringProperty linkProperty()
    {
        return link;
    }

    public static Callback<Link, Observable[]> extractor =
            l -> new Observable[]
                    {
                            l.nameProperty(), l.linkProperty()
                    };

    @Override
    public String toString() {
        return name.get() + " " + link.get();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Link link1 = (Link) o;
        return Objects.equals(name, link1.name) && Objects.equals(link, link1.link);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, link);
    }
}
