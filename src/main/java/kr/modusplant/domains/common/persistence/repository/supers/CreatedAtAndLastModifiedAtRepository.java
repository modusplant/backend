package kr.modusplant.domains.common.persistence.repository.supers;

public interface CreatedAtAndLastModifiedAtRepository<T> extends CreatedAtRepository<T>, LastModifiedAtRepository<T> {
}
