/*
 * Copyright (c) 2018 Orkhan Gasimli - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Written by Orkhan Gasimli orkhan.gasimli@gmail.com in 2018.
 */

package app.ogasimli.remoter.model.data.local.room

import androidx.room.*
import androidx.sqlite.db.SupportSQLiteQuery
import app.ogasimli.remoter.model.models.Job
import io.reactivex.Flowable

/**
 * Interface defining necessary methods for manipulating jobs table
 *
 * @author Orkhan Gasimli on 20.07.2018.
 */
@Dao
interface JobDao {

    /**
     * Get all job data from the table.
     *
     * @param query         custom {@link SimpleSQLiteQuery}
     * @return              the list of jobs retrieved from the table
     */
    @RawQuery(observedEntities = [Job::class])
    fun getAllJobs(query: SupportSQLiteQuery): Flowable<List<Job>>

    /**
     * Get all bookmarked job items.
     *
     * @param query         custom {@link SimpleSQLiteQuery}
     * @return              the list of bookmarked jobs retrieved from the table
     */
    @RawQuery(observedEntities = [Job::class])
    fun getBookmarkedJobs(query: SupportSQLiteQuery): Flowable<List<Job>>

    /**
     * Retrieve all jobs that match the given query text from the table.
     *
     * @param sortOption    column name for SQLite's ORDER BY command
     * @param query         query text
     * @return              the list of jobs retrieved from the table
     */
    @Query("""SELECT * FROM jobs
        WHERE position LIKE '%' || :query  || '%' OR tags LIKE '%' || :query  || '%'
        ORDER BY
        CASE :sortOption
        WHEN 'postingTime' THEN postingTime
        END DESC,
        CASE :sortOption
        WHEN 'position' THEN position
        WHEN 'company' THEN company
        END asc
        """)
    fun searchAllJobs(sortOption: String, query: String): Flowable<List<Job>>

    /**
     * Retrieve all bookmarked jobs that match the given query text from the table.
     *
     * @param sortOption    column name for SQLite's ORDER BY command
     * @param query         query text
     * @return              the list of bookmarked jobs retrieved from the table
     */
    @Query("""SELECT * FROM jobs
        WHERE isBookmarked = 1 AND
        (position LIKE '%' || :query  || '%' OR tags LIKE '%' || :query || '%')
        ORDER BY
        CASE :sortOption
        WHEN 'postingTime' THEN postingTime
        END DESC,
        CASE :sortOption
        WHEN 'position' THEN position
        WHEN 'company' THEN company
        END asc
        """)
    fun searchBookmarkedJobs(sortOption: String, query: String): Flowable<List<Job>>

    /**
     * Get data of a specific job from the table.
     *
     * @param jobId         id of the job to be deleted
     * @return              the job data from the table
     */
    @Query("SELECT * FROM jobs WHERE id=:jobId LIMIT 1")
    fun getJobById(jobId: String): Flowable<Job>

    /**
     * Insert a job to the database.
     * If the job data already exists, ignore transaction.
     *
     * @param job           the job to be inserted.
     * @return              list of id's of inserted jobs
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertJob(vararg job: Job): LongArray

    /**
     * Insert a job to the database.
     * If the job data already exists, replace it.
     *
     * @param job           the job to be inserted.
     * @return              list of id's of upserted jobs
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun upsertJob(vararg job: Job): LongArray

    /**
     * Delete specific job data.
     *
     * @param job           the job to be deleted
     * @return              the number of rows removed from the DB
     */
    @Delete
    fun deleteJob(vararg job: Job): Int

    /**
     * Delete jobs whose id is not present in table
     *
     * @param idList        the list of IDs of actual jobs
     * @return              the number of rows removed from the DB
     */
    @Query("DELETE FROM jobs WHERE id NOT IN(:idList)")
    fun deleteOldJobs(idList: List<String>): Int

    /**
     * Delete all job data.
     *
     * @return              the number of rows removed from the DB
     */
    @Query("DELETE FROM jobs")
    fun deleteAllJobs(): Int
}