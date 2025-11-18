package kr.modusplant.shared.persistence.repository;

public interface CreatedAtAndLastModifiedAtRepository<T> extends CreatedAtRepository<T>, LastModifiedAtRepository<T> {
}
