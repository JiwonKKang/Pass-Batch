package com.fastcampus.pass.job.pass;

import com.fastcampus.pass.repository.pass.*;
import com.fastcampus.pass.repository.user.UserGroupMapping;
import com.fastcampus.pass.repository.user.UserGroupMappingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class AddPassesTasklet implements Tasklet {

    private final UserGroupMappingRepository userGroupMappingRepository;
    private final PassRepository passRepository;
    private final BulkPassRepository bulkPassRepository;

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
        final LocalDateTime startedAt = LocalDateTime.now().minusDays(1);
        List<BulkPass> bulkPasses = bulkPassRepository.findByStatusAndStartedAtGreaterThan(BulkPassStatus.READY, startedAt);
        int count = 0;

        for (BulkPass bulkPassEntity : bulkPasses) {
            // user group에 속한 userId들을 조회합니다.
            final List<String> userIds = userGroupMappingRepository.findByUserGroupId(bulkPassEntity.getUserGroupId())
                    .stream().map(UserGroupMapping::getUserId).toList();

            // 각 userId로 이용권을 추가합니다.
            count += addPasses(bulkPassEntity, userIds);
            // pass 추가 이후 상태를 COMPLETED로 업데이트합니다.
            bulkPassEntity.setStatus(BulkPassStatus.COMPLETED);

        }
        log.info("AddPassesTasklet - execute: 이용권 {}건 추가 완료, startedAt={}", count, startedAt);
        return RepeatStatus.FINISHED;
    }

    private int addPasses(BulkPass bulkPass, List<String> userIds) {

        List<Pass> passList = userIds.stream()
                .map(userId -> PassModelMapper.INSTANCE.toPass(bulkPass, userId))
                .toList();
        return passRepository.saveAll(passList).size();
    }
}
