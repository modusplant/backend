package kr.modusplant.domains.tip.domain.service;

import kr.modusplant.domains.tip.app.http.request.FileOrder;
import kr.modusplant.domains.tip.app.http.request.TipPostRequest;
import kr.modusplant.domains.tip.persistence.entity.TipPostEntity;
import kr.modusplant.domains.tip.persistence.repository.TipPostRepository;
import kr.modusplant.global.error.EntityNotFoundWithUlidException;
import kr.modusplant.domains.tip.error.PostAccessDeniedException;
import kr.modusplant.global.error.InvalidInputException;
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
public class TipPostValidationService {

    private final TipPostRepository tipPostRepository;

    public void validateTipPostInsertRequest(TipPostRequest request) {
        validateGroupOrder(request.groupOrder(),true);
        validateTitle(request.title(),true);
        validateContentAndOrderInfo(request.content(),request.orderInfo(),true);
    }

    public void validateTipPostUpdateRequest(TipPostRequest request) {
        validateGroupOrder(request.groupOrder(),false);
        validateTitle(request.title(),false);
        validateContentAndOrderInfo(request.content(),request.orderInfo(),false);
    }

    public void validateAccessibleTipPost(String ulid, UUID memberUuid) {
        TipPostEntity tipPost = findValidByUuid(ulid);
        validateMemberHasPostAccess(tipPost,memberUuid);
    }

    public void validateNotFoundUlid(String ulid) {
        if (ulid == null || !tipPostRepository.existsByUlid(ulid)) {
            throw new EntityNotFoundWithUlidException(ulid, TipPostEntity.class);
        }
    }

    private void validateGroupOrder(Integer groupOrder, boolean isRequired) {
        if (groupOrder == null) {
            if (isRequired)
                throw new InvalidInputException("groupOrder",null,Integer.class);
        } else if (groupOrder < 0) {
            throw new InvalidInputException("groupOrder",groupOrder,Integer.class);
        }
    }

    private void validateTitle(String title, boolean isRequired) {
        if (title == null) {
            if (isRequired)
                throw new InvalidInputException("title",null,String.class);
        } else if (title.isBlank() || title.length() > 150) {
            throw new InvalidInputException("title",title,String.class);
        }
    }

    private void validateContentAndOrderInfo(List<MultipartFile> content, List<FileOrder> orderInfo, boolean isRequired) {
        boolean contentEmpty = content == null || content.isEmpty();
        boolean orderInfoEmpty = orderInfo==null || orderInfo.isEmpty();

        if (isRequired) {
            if (contentEmpty || orderInfoEmpty || isContentNotValid(content,orderInfo)) {
                throw new InvalidInputException("content", content, List.class);
            }
        } else {
            if (contentEmpty ^ orderInfoEmpty) {
                throw new InvalidInputException("A required field is missing");
            }
            if (!contentEmpty && !orderInfoEmpty && isContentNotValid(content, orderInfo)) {
                throw new InvalidInputException("content", content, List.class);
            }
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

    private TipPostEntity findValidByUuid(String ulid) {
        if (ulid == null) {
            throw new EntityNotFoundWithUlidException(ulid, TipPostEntity.class);
        }
        return tipPostRepository.findByUlid(ulid)
                .filter(tipPost -> !Boolean.TRUE.equals(tipPost.getIsDeleted()))
                .orElseThrow(() -> new EntityNotFoundWithUlidException(ulid,TipPostEntity.class));
    }

    private void validateMemberHasPostAccess(TipPostEntity tipPost, UUID memberUuid) {
        if(!tipPost.getAuthMember().getUuid().equals(memberUuid)) {
            throw new PostAccessDeniedException();
        }
    }
}
