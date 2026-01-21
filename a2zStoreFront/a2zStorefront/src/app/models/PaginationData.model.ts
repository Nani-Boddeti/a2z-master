export interface PaginationData {
    currentPage: number;
    totalPages: number;
    totalItems: number;
    itemsPerPage: number;
    hasPrevious: boolean;
    hasNext: boolean;
    pages: (number | string)[];
}