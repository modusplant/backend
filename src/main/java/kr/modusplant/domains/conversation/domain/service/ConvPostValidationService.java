package kr.modusplant.domains.conversation.domain.service;

import kr.modusplant.domains.conversation.app.http.request.FileOrder;
import kr.modusplant.domains.conversation.app.http.request.ConvPostRequest;
import kr.modusplant.domains.conversation.persistence.entity.ConvPostEntity;
import kr.modusplant.domains.conversation.persistence.repository.ConvPostRepository;
import kr.modusplant.global.error.EntityNotFoundWithUlidException;
import kr.modusplant.domains.conversation.error.PostAccessDeniedException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.UUID;
import java.util.List;


@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ConvPostValidationService {

    private final ConvPostRepository convPostRepository;

    public void validateConvPostRequest(ConvPostRequest request) {
        validateGroupOrder(request.groupOrder());
        validateTitle(request.title());
        validateContentAndOrderInfo(request.content(),request.orderInfo());
    }

    public void validateAccessibleConvPost(String ulid, UUID memberUuid) {
        ConvPostEntity convPost = findIfExistsByUlid(ulid);
        validateMemberHasPostAccess(convPost,memberUuid);
    }

    public void validateNotFoundUlid(String ulid) {
        if (ulid == null || !convPostRepository.existsByUlid(ulid)) {
            throw new EntityNotFoundWithUlidException(ulid, ConvPostEntity.class);
        }
    }

    private void validateGroupOrder(Integer groupOrder) {
        if (groupOrder == null || groupOrder < 0) {
            throw new IllegalArgumentException("groupOrder must not be null and must be a non-negative integer.");
        }
    }

    private void validateTitle(String title) {
        if (title == null || title.isBlank() || title.length() > 150) {
            throw new IllegalArgumentException("title must not be null or blank and must be at most 150 characters long.");
        }
    }

    private void validateContentAndOrderInfo(List<MultipartFile> content, List<FileOrder> orderInfo) {
        boolean contentEmpty = content == null || content.isEmpty();
        boolean orderInfoEmpty = orderInfo==null || orderInfo.isEmpty();
        if (contentEmpty || orderInfoEmpty || isContentNotValid(content,orderInfo)) {
            throw new IllegalArgumentException("Content and orderInfo must not be empty, and their filenames must match in size and order.");
        }
    }

    private boolean isContentNotValid(List<MultipartFile> content, List<FileOrder> orderInfo) {
        if(content.size() != orderInfo.size()) {
            return true;
        }

        List<String> contentFilenames = new ArrayList<>(content.size());
        for(MultipartFile part:content) {
            String fileName = part.getOriginalFilename();
            String contentType = part.getContentType();
            if (fileName == null || fileName.isEmpty() || contentType == null || contentType.isEmpty()) {
                return true;
            }
            contentFilenames.add(fileName);
        }

        List<String> orderFilenames = orderInfo.stream()
                .sorted(Comparator.comparingInt(FileOrder::order))
                .map(FileOrder::filename)
                .toList();

        return !contentFilenames.equals(orderFilenames);
    }

    private ConvPostEntity findIfExistsByUlid(String ulid) {
        if (ulid == null) {
            throw new EntityNotFoundWithUlidException(ulid, ConvPostEntity.class);
        }
        return convPostRepository.findByUlid(ulid)
                .filter(convPost -> !Boolean.TRUE.equals(convPost.getIsDeleted()))
                .orElseThrow(() -> new EntityNotFoundWithUlidException(ulid,ConvPostEntity.class));
    }

    // TODO : Spring Security 적용 후 PreAuthorize 고려
    private void validateMemberHasPostAccess(ConvPostEntity convPost, UUID memberUuid) {
        if(!convPost.getAuthMember().getUuid().equals(memberUuid)) {
            throw new PostAccessDeniedException();
        }
    }
}
