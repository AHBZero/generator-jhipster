package <%=packageName%>.domain;

import <%=packageName%>.service.util.ObjectId;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
<% if (searchEngine === 'elasticsearch') { %>import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Mapping;<% } %>

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.PostLoad;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "photo")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Photo implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    private Long id;

    @Column(name = "uuid")
    @JsonIgnore
    private String uuid;

    @Column(name = "thumbnail")
    private String thumbnail;

    @Column(name = "medium")
    private String medium;

    @Column(name = "original")
    private String original;

    @Column(name = "thumbnail_name")
    @JsonIgnore
    private String thumbnailName;

    @Column(name = "medium_name")
    @JsonIgnore
    private String mediumName;

    @Column(name = "original_name")
    @JsonIgnore
    private String originalName;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Lob
    @Column(name = "file")
    private byte[] file;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Column(name = "file_content_type")
    private String fileContentType;

    @Column(name = "processed")
    @JsonIgnore
    private Boolean processed = Boolean.FALSE;

    @PrePersist
    @PreUpdate
    public void generateUuid() {
        if (this.uuid == null) {
            this.uuid = ObjectId.get().toString() + RandomStringUtils.randomAlphabetic(5);
        }
        this.thumbnailName = uuid + "-thumb.png";
        this.mediumName = uuid + "-medium.png";
        this.originalName = uuid + "-original.png";
    }

    @PostLoad
    public void validate() {
        if (processed == null) {
            processed = Boolean.TRUE;
        }
    }

    public String getThumbnailName() {
        return thumbnailName;
    }

    public void setThumbnailName(String thumbnailName) {
        this.thumbnailName = thumbnailName;
    }

    public String getMediumName() {
        return mediumName;
    }

    public void setMediumName(String mediumName) {
        this.mediumName = mediumName;
    }

    public String getOriginalName() {
        return originalName;
    }

    public void setOriginalName(String originalName) {
        this.originalName = originalName;
    }

    public Photo createUuid() {
        generateUuid();
        return this;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public byte[] getFile() {
        return file;
    }

    public void setFile(byte[] file) {
        this.file = file;
    }

    public String getFileContentType() {
        return fileContentType;
    }

    public void setFileContentType(String fileContentType) {
        this.fileContentType = fileContentType;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getMedium() {
        return medium;
    }

    public void setMedium(String medium) {
        this.medium = medium;
    }

    public String getOriginal() {
        return original;
    }

    public void setOriginal(String original) {
        this.original = original;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
            .append("id", id)
            .append("thumbnail", thumbnail)
            .append("medium", medium)
            .append("original", original)
            .append("fileContentType", fileContentType)
            .toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Photo photo = (Photo) o;
        return Objects.equals(id, photo.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public Boolean isProcessed() {
        return processed;
    }

    public void setProcessed(Boolean processed) {
        this.processed = processed;
    }
}
