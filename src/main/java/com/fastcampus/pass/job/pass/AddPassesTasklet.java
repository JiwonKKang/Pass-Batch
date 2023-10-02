package com.fastcampus.pass.job.pass;

import com.fastcampus.pass.repository.pass.BulkPass;
import com.fastcampus.pass.repository.pass.BulkPassRepository;
import com.fastcampus.pass.repository.pass.BulkPassStatus;
import com.fastcampus.pass.repository.pass.PassRepository;
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

        bulkPasses.stream()
                .map(bulkPass -> userGroupMappingRepository.findByUserGroupId(bulkPass.getUserGroupId()))
        return null;
    }
}
