package kr.modusplant.shared.persistence.repository.supers;

public interface CreatedAtAndLastModifiedAtRepository<T> extends CreatedAtRepository<T>, LastModifiedAtRepository<T> {
}
