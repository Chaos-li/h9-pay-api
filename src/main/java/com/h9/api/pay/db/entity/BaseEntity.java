package com.h9.api.pay.db.entity;


import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * 基础实体类信息
 */
@MappedSuperclass
public class BaseEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", columnDefinition = "BIGINT COMMENT '主键'", nullable = false)
    protected Long id;

    @CreationTimestamp
    @Column(name = "create_time", columnDefinition = "DATETIME COMMENT '创建时间'", nullable = false, updatable = false)
    protected Date createTime;

    @UpdateTimestamp
    @Column(name = "update_time", columnDefinition = "DATETIME COMMENT '更新时间'")
    protected Date updateTime;

    @Version
    @Column(name = "version", columnDefinition = "INT COMMENT '版本号 '")
    protected Integer version;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }
}
